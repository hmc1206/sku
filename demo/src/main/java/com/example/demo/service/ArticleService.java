package com.example.demo.service;

import com.example.demo.domain.Article;
import com.example.demo.domain.Member;
import com.example.demo.repository.ArticleRespository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.utils.JwtUtil;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor //의존성 주입(final)
@Transactional(readOnly = true) //조회 전용으로 하나의 작업으로 묶음
public class ArticleService {
    private final ArticleRespository articleRespository;
    private final MemberService memberService;

    @Transactional
    public Article AddArticle(String token, String title, String content){
        Member member = memberService.tokenToMember(token);
        Article article = new Article(title, content, member); //새로운 객체
        return articleRespository.save(article); //저장~
    }

    @Transactional
    public Article updateArticle(Long articleId, String title, String content, String token) {
        Member member = memberService.tokenToMember(token);
        Optional<Article> article = articleRespository.findById(articleId); // 반환 타입 옵션널
        if(article.isEmpty()){
            return null;
        }
        if(article.get().getWriter() == member){ //객체를 꺼냄
            article.get().update(title, content); //update
        }
        return article.get(); //수정한 객체 반환
    }

    @Transactional
    public String deleteArticle(Long articleId, String token){
        Member member = memberService.tokenToMember(token);
        Optional<Article> article = articleRespository.findById(articleId);
        if(article.isEmpty()){
            return "해당 게시글이 없습니다.";
        }
        if(article.get().getWriter() == member){
            articleRespository.deleteById(articleId);
            return "성공적으로 삭제되었습니다.";
        } else{
            return "회원님의 게시글이 아닙니다.";
        }
    }

    public Article findById(Long articleId){
        Optional<Article> article = articleRespository.findById(articleId);
        if(article.isEmpty()){
            return null;
        }
        return articleRespository.findById(articleId).get(); //article.get()과 동일
    }

    public List<Article> findAll() { return articleRespository.findAll(); }

    public List<Article> findAllByWriter(String writerId){
        Member member = memberService.findByUserId(writerId);
        if(member == null){
            return List.of(); //불변
        }
        return articleRespository.findAllByWriter(member);
    }
}
