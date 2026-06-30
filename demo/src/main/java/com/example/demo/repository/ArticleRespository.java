package com.example.demo.repository;

import com.example.demo.domain.Article;
import com.example.demo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRespository extends JpaRepository<Article, Long> {
    List<Article> findAllByWriter(Member writer);
}
