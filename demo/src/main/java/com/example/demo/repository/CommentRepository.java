package com.example.demo.repository;

import com.example.demo.domain.Article;
import com.example.demo.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//jpaRepository에서 기본 crud기능을 자동으로 제공
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticle(Article article);
}
