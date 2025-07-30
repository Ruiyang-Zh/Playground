package cn.edu.nju.playground.model.dto.comment;

import cn.edu.nju.playground.model.dto.user.UserPublicInfoResponse;
import cn.edu.nju.playground.model.po.Comment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {

    private Long id;
    private String content;
    private UserPublicInfoResponse user;
    private Long parentId;
    private String parentUserName; // 父评论作者名称，方便显示
    private String parentContent; // 父评论内容，方便显示
    private LocalDateTime createdAt;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.user = new UserPublicInfoResponse(comment.getUser());
        this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
        this.parentContent = comment.getParent() != null ? comment.getParent().getContent() : null;
        this.parentUserName = comment.getParent() != null ? comment.getParent().getUser().getUsername() : null;
        this.createdAt = comment.getCreatedAt();
    }
}
