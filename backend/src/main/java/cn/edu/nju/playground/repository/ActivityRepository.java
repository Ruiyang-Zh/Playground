package cn.edu.nju.playground.repository;

import cn.edu.nju.playground.model.po.Activity;
import cn.edu.nju.playground.enums.ActivityStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {
    /**
     * 使用悲观锁获取活动
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Activity a WHERE a.id = :id")
    Optional<Activity> findByIdWithLock(@Param("id") Long id);


    /**
     * 根据创建者查询活动
     */
    Page<Activity> findByCreator_IdOrderByCreatedAtDesc(Long creatorId, Pageable pageable);

    /**
     * 根据状态查询活动
     */
    List<Activity> findByStatusIn(List<ActivityStatus> statuses);

    /**
     * 查询需要更新状态的活动
     */
    @Query("SELECT a FROM Activity a WHERE " +
            "(a.status = 'RECRUITING' AND a.registrationDeadline < :now) OR " +
            "(a.status IN ('RECRUITING', 'FULL', 'REGISTRATION_CLOSED') AND a.startTime < :now) OR " +
            "(a.status = 'IN_PROGRESS' AND a.endTime < :now)")
    List<Activity> findActivitiesNeedingStatusUpdate(@Param("now") LocalDateTime now);

    /**
     * 统计用户创建的活动数量
     */
    long countByCreator_Id(Long creatorId);

    /**
     * 查询即将开始的活动
     */
    @Query("SELECT a FROM Activity a WHERE a.startTime BETWEEN :start AND :end AND a.status IN ('RECRUITING', 'FULL', 'REGISTRATION_CLOSED')")
    List<Activity> findUpcomingActivities(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}