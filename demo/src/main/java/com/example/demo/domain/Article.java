package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Lazy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor //DB관련
@Entity
public class Article {
    @Id @GeneratedValue //기본키 생성, 자동 값 증가
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //관계, 지연 로딩
    @JoinColumn(name = "writer_id") //외래키 지정, 속성 이름 지정
    @OnDelete(action = OnDeleteAction.CASCADE) //무결성 제약 조건
    private Member  writer;

    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String title;
    @Lob // 대용량 메세지를 처리하기 위한 어노테이션
    private String content;

    public Article(String title, String content, Member writer){
        this.title = title;
        this.content = content;
        this.createDate = LocalDateTime.now();
        this.updateDate = this.createDate;
        this.writer = writer;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updateDate = LocalDateTime.now();
    }
}
