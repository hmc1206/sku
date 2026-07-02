package com.example.demo.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Comment {
    @Id @GeneratedValue
    private Long id; //댓글의 기본키

    //현재 엔티티가 N:1 관계를 가짐, 지연 로딩
    //외래키 매핑, 실제 컬럼명을 "writer_id로 지정
    //부모 엔티티 삭제시, 연관된 자식 엔티티도 삭제
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member writer; //이 댓글을 작성한 회원

    //게시글과 댓글은 어떤 관계일가? fetch 속성
    //외래키 매핑, 실제 컬럼명을 "article_id"로 지정
    //부모 엔티티 삭제시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Article article; // 댓글을 달 게시글

    private  String content; // 댓글 내용
    private LocalDateTime createDate; //댓글 작성 시간
    private LocalDateTime updateDate; //댓글 수정 시간

    public Comment(Member writer, Article article, String content){
        this.writer = writer;
        this.article = article;
        this.content = content;
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now(); //분기 처리
    }

    public void updateComment(String content){
        this.content = content;
        this.updateDate = LocalDateTime.now();
    }
}
