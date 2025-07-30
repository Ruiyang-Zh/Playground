package cn.edu.nju.playground.repository;

import cn.edu.nju.playground.model.po.Registration;
import cn.edu.nju.playground.model.po.Activity;
import cn.edu.nju.playground.enums.RegistrationStatus;
import cn.edu.nju.playground.model.po.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    /**
     * 查询活动的报名记录
     */
    @Query("SELECT r FROM Registration r " +
            "WHERE r.activity.id = :activityId " +
            "AND (:status IS NULL OR r.status = :status) " +
            "ORDER BY r.registeredAt ASC")
    List<Registration> findByActivity_IdAndStatus(@Param("activityId") Long activityId, @Param("status") RegistrationStatus status);

    /**
     * 查询用户的报名记录
     */
    @Query("SELECT r FROM Registration r " +
            "WHERE r.user.id = :userId " +
            "AND (:status IS NULL OR r.status = :status) " +
            "ORDER BY r.registeredAt DESC")
    Page<Registration> findByUser_IdAndStatus(Long userId, RegistrationStatus status, Pageable pageable);

    /**
     * 检查用户是否已报名活动
     */
    boolean existsByActivity_IdAndUser_Id(Long activityId, Long userId);

    /**
     * 查询用户在某活动的报名记录
     */
    Optional<Registration> findByActivity_IdAndUser_Id(Long activityId, Long userId);

    /**
     * 查询用户在某活动的特定状态的报名记录
     */
    Optional<Registration> findByActivity_IdAndUser_IdAndStatus(Long activityId, Long userId, RegistrationStatus status);

    /**
     * 根据活动状态查询用户已报名的活动
     */
    @Query("SELECT r.activity FROM Registration r " +
            "WHERE r.user.id = :userId " +
            "AND r.status = 'CONFIRMED' " +
            "AND (:activityStatus IS NULL OR r.activity.status = :activityStatus) "+
            "ORDER BY r.activity.startTime ASC")
    List<Activity> findActivitiesByUserIdAndStatus(@Param("userId") Long userId, @Param("activityStatus") String activityStatus);

    /**
     * 查询活动的参与者
     */
    @Query("SELECT r.user FROM Registration r WHERE r.activity.id = :activityId AND r.status = 'CONFIRMED' ORDER BY r.registeredAt ASC")
    List<User> findParticipantsByActivityId(@Param("activityId") Long activityId);

    Page<Registration> findAll(Specification<Registration> spec, Pageable pageable);
}