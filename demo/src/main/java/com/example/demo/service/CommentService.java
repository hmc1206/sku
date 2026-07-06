package com.example.demo.service;

import com.example.demo.domain.Article;
import com.example.demo.domain.Comment;
import com.example.demo.domain.Member;
import com.example.demo.exception.InvalidArticleIdException;
import com.example.demo.repository.ArticleRespository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //DB 작업의 원자적 실행 보장(일괄처리)
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final ArticleService articleService;

    @Transactional
    public Comment saveComment(String token, Long articleID, String content){
        //token에서 Member추출하는 메서드 이용해서 반환된 결과 member 변수에 저장
        Member member = memberService.tokenToMember(token);
        //articleId로 Article 찾는 메서드 이용해서 반환된 결과 article 변수에 저장
        Article article = articleService.findById(articleID);
        if(article == null){
            throw new InvalidArticleIdException("존재하지 않는 articleId");
        }
        Comment comment = new Comment(member, article, content);
        commentRepository.save(comment);
        return comment;
    }

    @Transactional
    public Comment updateComment(Long commentId, String token, String content){
        //commentId로 댓글을 조회하는 메서드 (findById)를 사용해서 반환된 결과를 optionalComment에 저장
        //Optional<Comment> optionalComment = commentRepository.findById(commentId);
        //optionalComment가 비어있다면(댓글 없음) null 반환
        //if(optionalComment.isEmpty()) return null;
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new InvalidArticleIdException("존재하지 않는 댓글입니다."))

        //token에서 회원을 추출하는 메서드(tokenToMember)를 사용해서 반환된 결과를 member에 저장
        Member member = memberService.tokenToMember(token);
        //member가 없다면 (토큰이 유효하지 않거나 회원 없음) return null
        if(member == null) {
            throw new InvalidArticleIdException("인증되지 않은 사용자입니다.")
        }

        //optionalComment에서 댓글(Comment)을 거내 comment 변수에 저장
        //Comment comment = optionalComment.get();

        //comment의 작성자(writer)와 token에서 추출한 member가 같은지 (id 비교로)확인 같지 않다면 null 반환
        if(!comment.getWriter().getId().equals(member.getId())){
            throw new InvalidArticleIdException("댓글 수정 권한이 없습니다.");
        }

        //같다면 comment의 content를 수정(updateComment(content))진행합니다.
        comment.updateComment(content);

        //수정된 comment 반환
        return comment;
    }

    public List<Comment> articleToComment(Long articleId){
        //article로 게시글을 조회하는 메서드(findById)를 사용해서 반환된 결과를 article 변수에 저장
        Article article = articleService.findById(articleId);
        //article이 존재하지 않으면 빈 리스트(List.og())반환
        if(article == null ) return List.of();
        //article을 조건으로 댓글을 조회하는 메서드(findbyArticle)를 사용해서 해당 게시글의 댓글 목록 반환
        return commentRepository.findByArticle(article);
    }

    @Transactional
    public String deleteComment(Long commentId, String token){
        //
       Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new InvalidArticleIdException("존재하지 않는 댓글입니다."));
        //if(optionalComment == null) return "존재하지 않은 댓글입니다";
        Member member = memberService.tokenToMember(token);
        if(member == null){
            throw new InvalidArticleIdException("인증되지 않은 사용자입니다.");
        }

        //optionalComment에서 댓글(Comment)을 꺼내 comment 변수에 저장
        //Comment comment = optionalComment.get();

        //comment의 작성자 (writer)와 token에서 추출한 member가 같은지(id비교로)확인 같지 않다면 -> "댓글 삭제 권한이 없습니다"봔한
        if(!comment.getWriter().getId().equals(member.getId())){
            throw new InvalidArticleIdException("댓글 삭제 권한이 없습니다");
        }

        //commentRepository(comment)를 호출해 댓글 삭제
        commentRepository.delete(comment);
        return "댓글이 삭제되었습니다.";
    }
}
