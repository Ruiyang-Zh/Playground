package cn.edu.nju.playground.repository;

import cn.edu.nju.playground.model.po.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.id = :id AND c.content IS NOT NULL")
    Optional<Comment> findByIdAndPresent(Long id);

    /**
     * 分页查询活动的所有评论，按创建时间倒序
     */
    @Query("SELECT c FROM Comment c WHERE c.activity.id = :activityId AND c.content IS NOT NULL")
    Page<Comment> findByActivity_Id(Long activityId, Pageable pageable);

    /**
     * 统计活动的评论总数
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.activity.id = :activityId AND c.content IS NOT NULL")
    long countByActivity_Id(Long activityId);

    /**
     * 查询评论的回复数量
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parent.id = :parentId AND c.content IS NOT NULL")
    long countByParent_Id(Long parentId);

    /**
     * 删除活动的所有评论
     */
    void deleteByActivity_Id(Long activityId);
}
