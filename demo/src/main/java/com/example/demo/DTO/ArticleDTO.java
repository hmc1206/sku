package com.example.demo.DTO;

import com.example.demo.domain.Article;
import lombok.Data;

import java.time.LocalDateTime;

public class ArticleDTO {
    @Data
    public static class ArticleRes {
        private String title;
        private String content;
        private String writer;
        private LocalDateTime createDate;
        private boolean isChange;

        public ArticleRes(Article article){
            this.title = article.getTitle();
            this.content = article.getContent();
            this.writer = article.getWriter().getUsername();
            this.createDate = article.getCreateDate();

            if(article.getCreateDate().equals(article.getUpdateDate())){
                this.isChange = false;
            }else{
                this.isChange = true;
            }
        }
    }

    @Data
    public static class ArticleReq{
        private Long articleId;
        private String title;
        private String content;
    }

    @Data
    public static class AddArticleReq{
        private String title;
        private String content;
    }
}
