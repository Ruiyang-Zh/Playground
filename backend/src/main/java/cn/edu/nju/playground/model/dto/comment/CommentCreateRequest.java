package cn.edu.nju.playground.model.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateRequest {

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容不能超过1000字符")
    private String content;

    private Long parentId; // 父评论ID，为空表示顶级评论
}