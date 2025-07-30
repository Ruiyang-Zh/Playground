package cn.edu.nju.playground.service;

import cn.edu.nju.playground.enums.UserActivityType;
import cn.edu.nju.playground.event.activity.RegistrationEvent;
import cn.edu.nju.playground.event.activity.UnregistrationEvent;
import cn.edu.nju.playground.exception.PlaygroundException;
import cn.edu.nju.playground.model.dto.activity.*;
import cn.edu.nju.playground.model.po.Activity;
import cn.edu.nju.playground.model.po.Registration;
import cn.edu.nju.playground.model.po.User;
import cn.edu.nju.playground.repository.ActivityRepository;
import cn.edu.nju.playground.repository.RegistrationRepository;
import cn.edu.nju.playground.util.FileUtil;
import cn.edu.nju.playground.util.TokenUtil;
import cn.edu.nju.playground.enums.ActivityStatus;
import cn.edu.nju.playground.enums.RegistrationStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final RegistrationRepository registrationRepository;
    private final TokenUtil tokenUtil;
    private final FileUtil fileUtil;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 创建活动
     */
    @Transactional
    public Long createActivity(ActivityCreateRequest request) {
        User creator = tokenUtil.getCurrentUser();
        if (creator == null) {
            throw PlaygroundException.notLoggedIn();
        }

        // 验证活动时间
        validateActivityTimes(request.getStartTime(), request.getEndTime(), request.getRegistrationDeadline());

        // 验证人数设置
        if (request.getMaxParticipants() < request.getMinParticipants()) {
            throw PlaygroundException.badRequest("最大参与人数不能小于最小参与人数");
        }

        // 处理图片上传
        List<String> imageUrls = handleImageUploads(creator, request.getImages());

        // 创建活动
        Activity activity = Activity.builder()
                .creator(creator)
                .contactInfo(request.getContactInfo())
                .title(request.getTitle())
                .type(request.getType())
                .description(request.getDescription())
                .images(imageUrls)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .province(request.getProvince())
                .city(request.getCity())
                .district(request.getDistrict())
                .venue(request.getVenue())
                .minParticipants(request.getMinParticipants())
                .maxParticipants(request.getMaxParticipants())
                .currentParticipants(1) // 创建者自动参与
                .fee(request.getFee())
                .registrationDeadline(request.getRegistrationDeadline())
                .requirements(request.getRequirements())
                .status(ActivityStatus.RECRUITING)
                .build();

        activity = activityRepository.save(activity);

        // 创建者自动报名（不收费）
        Registration creatorRegistration = Registration.builder()
                .activity(activity)
                .user(creator)
                .status(RegistrationStatus.CONFIRMED)
                .feeAmount(BigDecimal.ZERO)
                .build();
        registrationRepository.save(creatorRegistration);

        log.info("活动创建成功，活动ID: {}, 创建者: {}", activity.getId(), creator.getId());
        return activity.getId();
    }

    /**
     * 查询活动列表
     */
    public Page<ActivityBriefResponse> getActivities(ActivityQueryRequest request, int page, int size) {
        User currentUser = tokenUtil.getCurrentUser();

        // 构建查询条件（包含关键字）
        Specification<Activity> spec = buildActivitySpecification(request);

        // 构建排序条件
        Sort sort = buildSort(request.getSortBy(), request.getSortDir());
        Pageable pageable = PageRequest.of(page, size, sort);

        // 执行查询
        Page<Activity> activities = activityRepository.findAll(spec, pageable);

        // 转换为响应对象
        return activities.map(activity -> {
            return new ActivityBriefResponse(activity, isUserRegistered(activity, currentUser));
        });
    }

    /**
     * 获取活动详情
     */
    public ActivityDetailResponse getActivityDetail(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> PlaygroundException.operationFailed("活动不存在"));

        User currentUser = tokenUtil.getCurrentUser();

        // 获取参与者列表
        List<User> participants = registrationRepository.findParticipantsByActivityId(activityId);

        return new ActivityDetailResponse(
                activity,
                participants,
                isUserRegistered(activity, currentUser),
                canUserRegister(activity, currentUser),
                canUpdateActivity(activity, currentUser),
                canCancelActivity(activity, currentUser)
        );
    }

    /**
     * 更新活动信息
     */
    @Transactional
    public void updateActivity(Long activityId, ActivityUpdateRequest request) {
        User currentUser = tokenUtil.getCurrentUser();

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> PlaygroundException.operationFailed("活动不存在"));

        // 检查权限
        if (!activity.getCreator().getId().equals(currentUser.getId())) {
            throw PlaygroundException.operationNotAllowed("只有活动创建者可以修改活动");
        }

        // 检查是否可以修改
        if (!canUpdateActivity(activity, currentUser)) {
            throw PlaygroundException.operationNotAllowed("活动报名截止后不能修改");
        }

        // 更新活动信息
        if (request.getTitle() != null) {
            if (request.getTitle().trim().isEmpty()) {
                throw PlaygroundException.badRequest("活动标题不能为空");
            }
            activity.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            activity.setDescription(request.getDescription());
        }
        if (request.getStartTime() != null) {
            validateActivityTimes(request.getStartTime(),
                    request.getEndTime() != null ? request.getEndTime() : activity.getEndTime(),
                    request.getRegistrationDeadline() != null ? request.getRegistrationDeadline() : activity.getRegistrationDeadline());
            activity.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            validateActivityTimes(activity.getStartTime(),
                    request.getEndTime(),
                    request.getRegistrationDeadline() != null ? request.getRegistrationDeadline() : activity.getRegistrationDeadline());
            activity.setEndTime(request.getEndTime());
        }
        if (request.getRegistrationDeadline() != null) {
            validateActivityTimes(
                    activity.getStartTime(),
                    activity.getEndTime(),
                    request.getRegistrationDeadline()
            );
            activity.setRegistrationDeadline(request.getRegistrationDeadline());
        }
        if (request.getProvince() != null) {
            if (request.getProvince().trim().isEmpty()) {
                throw PlaygroundException.badRequest("省份不能为空");
            }
            activity.setProvince(request.getProvince());
        }
        if (request.getCity() != null) {
            if (request.getCity().trim().isEmpty()) {
                throw PlaygroundException.badRequest("城市不能为空");
            }
            activity.setCity(request.getCity());
        }
        if (request.getDistrict() != null) {
            if (request.getDistrict().trim().isEmpty()) {
                throw PlaygroundException.badRequest("地区不能为空");
            }
            activity.setDistrict(request.getDistrict());
        }
        if (request.getVenue() != null) {
            if (request.getVenue().trim().isEmpty()) {
                throw PlaygroundException.badRequest("活动场地不能为空");
            }
            activity.setVenue(request.getVenue());
        }
        if (request.getMaxParticipants() != null) {
            if (request.getMaxParticipants() < activity.getCurrentParticipants()) {
                throw PlaygroundException.badRequest("最大参与人数不能小于当前参与人数");
            }
            activity.setMaxParticipants(request.getMaxParticipants());
        }
        if (request.getMinParticipants() != null) {
            if (request.getMinParticipants() < 1) {
                throw PlaygroundException.badRequest("最小参与人数不能小于1");
            }
            if (request.getMaxParticipants() != null && request.getMinParticipants() > request.getMaxParticipants()) {
                throw PlaygroundException.badRequest("最小参与人数不能大于最大参与人数");
            }
            activity.setMinParticipants(request.getMinParticipants());
        }
        if (request.getContactInfo() != null) {
            if (request.getContactInfo().trim().isEmpty()) {
                throw PlaygroundException.badRequest("联系方式不能为空");
            }
            activity.setContactInfo(request.getContactInfo());
        }
        if (request.getRequirements() != null) {
            activity.setRequirements(request.getRequirements());
        }

        // 处理图片更新
        if (request.getImages() != null) {
            // 删除旧图片
            if (activity.getImages() != null) {
                for (String imageUrl : activity.getImages()) {
                    fileUtil.delete(imageUrl);
                }
            }
            // 上传新图片
            activity.setImages(handleImageUploads(currentUser, request.getImages()));
        }

        activityRepository.save(activity);
        log.info("活动更新成功，活动ID: {}", activityId);
    }

    /**
     * 取消活动
     */
    @Transactional
    public void cancelActivity(Long activityId) {
        User currentUser = tokenUtil.getCurrentUser();
        if (currentUser == null) {
            throw PlaygroundException.notLoggedIn();
        }

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> PlaygroundException.operationFailed("活动不存在"));

        // 检查权限
        if (!activity.getCreator().getId().equals(currentUser.getId())) {
            throw PlaygroundException.operationNotAllowed("只有活动创建者可以取消活动");
        }

        // 检查是否可以取消
        if (!canCancelActivity(activity, currentUser)) {
            throw PlaygroundException.operationNotAllowed("活动开始前2小时内不能取消");
        }

        cancelActivityInternal(activity);
    }

    private void cancelActivityInternal(Activity activity) {
        // 处理报名记录
        List<Registration> registrations = registrationRepository
                .findByActivity_IdAndStatus(activity.getId(), RegistrationStatus.CONFIRMED);

        for (Registration registration : registrations) {
            registration.setStatus(RegistrationStatus.CANCELLED);
            // 退还费用
            if (registration.getFeeAmount().compareTo(BigDecimal.ZERO) > 0) {
                eventPublisher.publishEvent(
                        UnregistrationEvent.builder()
                                .registration(registration)
                                .description("活动取消退费")
                                .build()
                );
            }
        }
        registrationRepository.saveAll(registrations);

        // 更新活动状态
        activity.setStatus(ActivityStatus.CANCELLED);
        activityRepository.save(activity);

        log.info("活动取消成功，活动ID: {}", activity.getId());
    }

    /**
     * 报名活动
     */
    @Transactional
    public void registerActivity(Long activityId) {
        User currentUser = tokenUtil.getCurrentUser();
        if (currentUser == null) {
            throw PlaygroundException.notLoggedIn();
        }

        // 使用悲观锁获取活动，防止并发问题
        Activity activity = activityRepository.findByIdWithLock(activityId)
                .orElseThrow(() -> PlaygroundException.operationFailed("活动不存在"));

        // 检查是否可以报名
        if (!canUserRegister(activity, currentUser)) {
            throw PlaygroundException.operationNotAllowed("无法报名此活动");
        }

        // 检查是否已经报名（包括待处理状态）
        if (registrationRepository.existsByActivity_IdAndUser_Id(activity.getId(), currentUser.getId())) {
            throw PlaygroundException.operationNotAllowed("您已经报名了此活动或有待处理的报名");
        }

        // 再次检查满员状态（关键的并发检查点）
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw PlaygroundException.operationNotAllowed("活动已满员");
        }

        // 创建报名记录
        Registration registration = Registration.builder()
                .activity(activity)
                .user(currentUser)
                .status(RegistrationStatus.PENDING)
                .feeAmount(activity.getFee())
                .build();
        registrationRepository.save(registration);

        // 收费处理
        if (activity.getFee().compareTo(BigDecimal.ZERO) > 0) {
            eventPublisher.publishEvent(
                    RegistrationEvent.builder()
                            .registration(registration)
                            .description("报名活动：" + activity.getTitle())
                            .build()
            );
        } else {
            // 如果活动免费，直接确认报名
            confirmRegistration(registration);
        }

        log.info("用户发起报名，活动ID: {}, 用户ID: {}, 报名ID: {}",
                activityId, currentUser.getId(), registration.getId());
    }

    /**
     * 确认报名（回调处理函数）
     */
    @Transactional
    public void confirmRegistration(Registration registration) {
        // 检查状态
        if (registration.getStatus() != RegistrationStatus.PENDING) {
            log.warn("尝试确认非待处理状态的报名，报名ID: {}, 当前状态: {}",
                    registration.getId(), registration.getStatus());
            return;
        }

        // 使用悲观锁重新获取活动
        Activity activity = activityRepository.findByIdWithLock(registration.getActivity().getId())
                .orElseThrow(() -> PlaygroundException.operationFailed("活动不存在"));

        // 再次检查是否满员（关键的并发检查点）
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            // 满员了，取消报名并退费
            registration.setStatus(RegistrationStatus.CANCELLED);
            registrationRepository.save(registration);

            if (registration.getFeeAmount().compareTo(BigDecimal.ZERO) > 0) {
                eventPublisher.publishEvent(
                        UnregistrationEvent.builder()
                                .registration(registration)
                                .description("活动满员退费：" + activity.getTitle())
                                .build()
                );
            }

            log.info("活动满员，报名失败，报名ID: {}", registration.getId());
            throw PlaygroundException.operationNotAllowed("活动已满员，报名失败，费用将退还");
        }

        // 确认报名
        registration.setStatus(RegistrationStatus.CONFIRMED);
        registrationRepository.save(registration);

        // 更新活动参与人数
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);

        // 检查是否满员
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            activity.setStatus(ActivityStatus.FULL);
        }

        activityRepository.save(activity);

        log.info("用户报名确认成功，活动ID: {}, 用户ID: {}",
                activity.getId(), registration.getUser().getId());
    }

    /**
     * 取消报名（扣费失败后的回调）
     */
    @Transactional
    public void cancelRegistration(Registration registration, String reason) {
        if (registration.getStatus() != RegistrationStatus.PENDING) {
            log.warn("尝试取消非待处理状态的报名，报名ID: {}, 当前状态: {}",
                    registration.getId(), registration.getStatus());
            return;
        }

        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);

        log.info("用户报名取消，活动ID: {}, 用户ID: {}, 原因: {}",
                registration.getActivity().getId(), registration.getUser().getId(), reason);
    }

    /**
     * 退出活动
     */
    @Transactional
    public void unregisterActivity(Long activityId) {
        User currentUser = tokenUtil.getCurrentUser();
        if (currentUser == null) {
            throw PlaygroundException.notLoggedIn();
        }

        // 使用悲观锁获取活动
        Activity activity = activityRepository.findByIdWithLock(activityId)
                .orElseThrow(() -> PlaygroundException.operationFailed("活动不存在"));

        // 创建者不能退出自己的活动
        if (activity.getCreator().getId().equals(currentUser.getId())) {
            throw PlaygroundException.operationNotAllowed("创建者不能退出自己的活动");
        }

        Registration registration = registrationRepository
                .findByActivity_IdAndUser_IdAndStatus(activityId, currentUser.getId(), RegistrationStatus.CONFIRMED)
                .orElseThrow(() -> PlaygroundException.operationNotAllowed("您还未报名此活动"));

        // 检查是否可以退出（报名截止时间前）
        if (LocalDateTime.now().isAfter(activity.getRegistrationDeadline())) {
            throw PlaygroundException.operationNotAllowed("报名截止后不能退出活动");
        }

        // 退还费用
        if (registration.getFeeAmount().compareTo(BigDecimal.ZERO) > 0) {
            eventPublisher.publishEvent(
                    UnregistrationEvent.builder()
                            .registration(registration)
                            .description("退出活动退费：" + activity.getTitle())
                            .build()
            );
        }

        // 取消报名
        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);

        // 更新活动参与人数
        activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);

        // 更新活动状态
        if (activity.getStatus() == ActivityStatus.FULL) {
            activity.setStatus(ActivityStatus.RECRUITING);
        }

        activityRepository.save(activity);

        log.info("用户退出活动成功，活动ID: {}, 用户ID: {}", activityId, currentUser.getId());
    }

    /**
     * 批量更新过期活动状态（定时任务调用）
     */
    @Transactional
    public void updateExpiredActivitiesStatus() {
        LocalDateTime now = LocalDateTime.now();
        List<Activity> activities = activityRepository.findActivitiesNeedingStatusUpdate(now);

        for (Activity activity : activities) {
            updateExpiredStatus(activity);
        }
    }

    @Transactional
    public void updateExpiredStatus(Activity activity) {
        ActivityStatus oldStatus = activity.getStatus();
        LocalDateTime now = LocalDateTime.now();

        if (activity.getEndTime().isBefore(now)) {
            activity.setStatus(ActivityStatus.COMPLETED);
        } else if (activity.getStartTime().isBefore(now)) {
            activity.setStatus(ActivityStatus.IN_PROGRESS);
        } else if (activity.getRegistrationDeadline().isBefore(now)) {
            // 检查是否达到最小人数
            if (activity.getCurrentParticipants() < activity.getMinParticipants()) {
                // 人数不足，取消活动
                log.info("活动因人数不足自动取消，活动ID: {}", activity.getId());
                cancelActivityInternal(activity);
            } else {
                activity.setStatus(ActivityStatus.REGISTRATION_CLOSED);
            }
        }

        if (!oldStatus.equals(activity.getStatus())) {
            activityRepository.save(activity);
            log.info("活动状态更新，活动ID: {}, {} -> {}",
                    activity.getId(), oldStatus, activity.getStatus());
        }
    }

    /**
     * 查询用户活动
     * @param userId 用户ID
     * @param request 查询条件
     * @param page 页码
     * @param size 页大小
     */
    @Transactional(readOnly = true)
    public Page<ActivityBriefResponse> getUserActivities(Long userId, UserActivityQueryRequest request, int page, int size) {
        User currentUser = tokenUtil.getCurrentUser();

        // 权限检查：创建的活动所有人可见，其他仅个人可见
        if (request.getParticipationType() != UserActivityType.CREATED) {
            if (currentUser == null || !currentUser.getId().equals(userId)) {
                throw PlaygroundException.operationNotAllowed("只能查看自己的参与活动");
            }
        }

        // 根据参与类型选择不同的查询逻辑
        if (request.getParticipationType() == UserActivityType.CREATED) {
            return queryCreatedActivities(userId, request, page, size, currentUser);
        } else {
            return queryJoinedActivities(userId, request, page, size, currentUser);
        }
    }

    /**
     * 查询创建的活动
     */
    private Page<ActivityBriefResponse> queryCreatedActivities(Long userId, UserActivityQueryRequest request,
                                                               int page, int size, User currentUser) {
        Specification<Activity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 创建者条件
            predicates.add(criteriaBuilder.equal(root.get("creator").get("id"), userId));

            // 添加活动筛选条件
            addActivityFilters(predicates, root, criteriaBuilder, request);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = buildUserActivitySort(request.getSortBy(), request.getSortDir(), true);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Activity> activities = activityRepository.findAll(spec, pageable);

        return activities.map(activity -> new ActivityBriefResponse(activity, isUserRegistered(activity, currentUser)));
    }

    /**
     * 查询参与的活动
     */
    private Page<ActivityBriefResponse> queryJoinedActivities(Long userId, UserActivityQueryRequest request,
                                                              int page, int size, User currentUser) {
        // 确定报名状态
        RegistrationStatus registrationStatus = getRegistrationStatusByType(request.getParticipationType());

        Specification<Registration> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 用户条件
            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));

            // 报名状态条件
            predicates.add(criteriaBuilder.equal(root.get("status"), registrationStatus));

            // 活动筛选条件
            Join<Registration, Activity> activityJoin = root.join("activity");
            addActivityJoinFilters(predicates, activityJoin, criteriaBuilder, request);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = buildUserActivitySort(request.getSortBy(), request.getSortDir(), false);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Registration> registrations = registrationRepository.findAll(spec, pageable);

        return registrations.map(registration -> {
            Activity activity = registration.getActivity();
            return new ActivityBriefResponse(activity, isUserRegistered(activity, currentUser));
        });
    }

    /**
     * 根据参与类型获取对应的报名状态
     */
    private RegistrationStatus getRegistrationStatusByType(UserActivityType participationType) {
        switch (participationType) {
            case JOINED:
                return RegistrationStatus.CONFIRMED;
            case APPLIED:
                return RegistrationStatus.PENDING;
            case CANCELLED:
                return RegistrationStatus.CANCELLED;
            default:
                throw PlaygroundException.badRequest("无效的参与类型");
        }
    }

    /**
     * 添加活动筛选条件（直接查询Activity）
     */
    private void addActivityFilters(List<Predicate> predicates, Root<Activity> root,
                                    CriteriaBuilder criteriaBuilder, UserActivityQueryRequest request) {
        if (request.getActivityType() != null) {
            predicates.add(criteriaBuilder.equal(root.get("type"), request.getActivityType()));
        }

        if (request.getActivityStatus() != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), request.getActivityStatus()));
        }

        if (request.getStartDate() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), request.getStartDate()));
        }

        if (request.getEndDate() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startTime"), request.getEndDate()));
        }

        if (request.getProvince() != null && !request.getProvince().trim().isEmpty()) {
            predicates.add(criteriaBuilder.equal(root.get("province"), request.getProvince()));
        }

        if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
            predicates.add(criteriaBuilder.equal(root.get("city"), request.getCity()));
        }
    }

    /**
     * 添加活动筛选条件（通过Join查询）
     */
    private void addActivityJoinFilters(List<Predicate> predicates, Join<Registration, Activity> activityJoin,
                                        CriteriaBuilder criteriaBuilder, UserActivityQueryRequest request) {
        if (request.getActivityType() != null) {
            predicates.add(criteriaBuilder.equal(activityJoin.get("type"), request.getActivityType()));
        }

        if (request.getActivityStatus() != null) {
            predicates.add(criteriaBuilder.equal(activityJoin.get("status"), request.getActivityStatus()));
        }

        if (request.getStartDate() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(activityJoin.get("startTime"), request.getStartDate()));
        }

        if (request.getEndDate() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(activityJoin.get("startTime"), request.getEndDate()));
        }

        if (request.getProvince() != null && !request.getProvince().trim().isEmpty()) {
            predicates.add(criteriaBuilder.equal(activityJoin.get("province"), request.getProvince()));
        }

        if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
            predicates.add(criteriaBuilder.equal(activityJoin.get("city"), request.getCity()));
        }
    }

    /**
     * 构建用户活动排序条件
     */
    private Sort buildUserActivitySort(String sortBy, String sortDir, boolean isCreatedQuery) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

        switch (sortBy) {
            case "time":
                // 创建的活动按创建时间排序，参与的活动按报名时间排序
                return isCreatedQuery ?
                        Sort.by(direction, "createdAt") :
                        Sort.by(direction, "registeredAt");
            case "activityTime":
                // 按活动开始时间排序
                return isCreatedQuery ?
                        Sort.by(direction, "startTime") :
                        Sort.by(direction, "activity.startTime");
            case "fee":
                // 按费用排序
                return isCreatedQuery ?
                        Sort.by(direction, "fee") :
                        Sort.by(direction, "activity.fee");
            default:
                return isCreatedQuery ?
                        Sort.by(direction, "createdAt") :
                        Sort.by(direction, "registeredAt");
        }
    }

    // 私有辅助方法

    private List<String> handleImageUploads(User user, List<MultipartFile> images) {
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imageUrl = fileUtil.upload(user.getId(), image);
                    if (imageUrl != null) {
                        imageUrls.add(imageUrl);
                    }
                }
            }
        }
        return imageUrls;
    }

    private void validateActivityTimes(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime registrationDeadline) {
        LocalDateTime now = LocalDateTime.now();

        if (startTime.isBefore(now)) {
            throw PlaygroundException.badRequest("活动开始时间不能是过去时间");
        }

        if (endTime.isBefore(startTime)) {
            throw PlaygroundException.badRequest("活动结束时间必须晚于开始时间");
        }

        if (registrationDeadline.isBefore(now)) {
            throw PlaygroundException.badRequest("报名截止时间不能是过去时间");
        }

        if (registrationDeadline.isAfter(startTime)) {
            throw PlaygroundException.badRequest("报名截止时间必须早于活动开始时间");
        }
    }

    private boolean isUserRegistered(Activity activity, User user) {
        if (activity == null || user == null) {
            return false;
        }
        return registrationRepository.existsByActivity_IdAndUser_Id(activity.getId(), user.getId());
    }

    private boolean canUserRegister(Activity activity, User user) {
        if (activity == null || user == null) {
            return false;
        }

        // 检查活动状态
        if (activity.getStatus() != ActivityStatus.RECRUITING) {
            return false;
        }

        // 检查报名截止时间
        if (LocalDateTime.now().isAfter(activity.getRegistrationDeadline())) {
            return false;
        }

        // 检查是否已报名
        return !isUserRegistered(activity, user);
    }

    private boolean canUpdateActivity(Activity activity, User user) {
        if (activity == null || user == null) {
            return false;
        }

        if (!activity.getCreator().getId().equals(user.getId())) {
            return false;
        }
        // 报名截止前可以修改
        return LocalDateTime.now().isBefore(activity.getRegistrationDeadline());
    }

    private boolean canCancelActivity(Activity activity, User user) {
        if (activity == null || user == null) {
            return false;
        }

        if (!activity.getCreator().getId().equals(user.getId())) {
            return false;
        }
        // 活动开始前2小时可以取消
        return LocalDateTime.now().isBefore(activity.getStartTime().minusHours(2));
    }

    private Specification<Activity> buildActivitySpecification(ActivityQueryRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键字搜索
            if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
                String likePattern = "%" + request.getKeyword().trim() + "%";
                Predicate titlePredicate = criteriaBuilder.like(root.get("title"), likePattern);
                Predicate descriptionPredicate = criteriaBuilder.like(root.get("description"), likePattern);
                Predicate venuePredicate = criteriaBuilder.like(root.get("venue"), likePattern);
                Predicate creatorPredicate = criteriaBuilder.like(root.get("creator").get("username"), likePattern);

                predicates.add(criteriaBuilder.or(titlePredicate, descriptionPredicate, venuePredicate, creatorPredicate));
            }

            // 活动类型
            if (request.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }

            // 开始时间范围
            if (request.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), request.getStartDate()));
            }
            if (request.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startTime"), request.getEndDate()));
            }

            // 费用范围
            if (request.getMinFee() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fee"), request.getMinFee()));
            }
            if (request.getMaxFee() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fee"), request.getMaxFee()));
            }

            // 地区
            if (request.getProvince() != null && !request.getProvince().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("province"), request.getProvince()));
            }
            if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("city"), request.getCity()));
            }
            if (request.getDistrict() != null && !request.getDistrict().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("district"), request.getDistrict()));
            }

            // 活动状态
            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Sort buildSort(String sortBy, String sortDir) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

        switch (sortBy) {
            case "fee":
                return Sort.by(direction, "fee");
            case "createdAt":
                return Sort.by(direction, "createdAt");
            case "startTime":
            default:
                return Sort.by(direction, "startTime");
        }
    }
}