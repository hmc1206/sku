package com.example.demo.DTO;

import com.example.demo.domain.Comment;
import lombok.Data;

import java.time.LocalDateTime;

public class CommentDTO {
    @Data
    public static  class CommentCreateRequest {
        private Long articleId;
        private String content;
    }

    @Data
    public static class CommentUpdateRequest {
        private Long commentId;
        private String content;
    }

    @Data
    public static class CommentResponse{
        private String content;
        private LocalDateTime createDate;
        private boolean isUpdate;
        private String writer;
//        private String writer_id;

        public CommentResponse(Comment comment){
            this.content = comment.getContent();
            this.createDate = comment.getCreateDate();
            //삼항연산자를 사용하여 댓글의 작성일과 수정일이 같아면 false,다르면 true로 해서 수정 여부 표시
            this.isUpdate = !comment.getUpdateDate().equals(comment.getUpdateDate());
            this.writer = comment.getWriter().getUsername();
//            this.writer_id = comment.getWriter().getUserId();
        }
    }
}
