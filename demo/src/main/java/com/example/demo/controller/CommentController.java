package com.example.demo.controller;

import com.example.demo.DTO.CommentDTO;
import com.example.demo.domain.Comment;
import com.example.demo.service.CommentService;
import com.example.demo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    @PostMapping("/comment")
    public ResponseEntity<CommentDTO.CommentResponse> createComment(@RequestHeader("Authorization") String token, @RequestBody CommentDTO.CommentCreateRequest request){
        if(!jwtUtil.validateJwt(token)){
            return null;
        }
        Comment comment = commentService.saveComment(token, request.getArticleId(), request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommentDTO.CommentResponse(comment));
    }

    @PutMapping("/comment/update")
    public ResponseEntity <CommentDTO.CommentResponse> updateComment(@RequestHeader("Authorization") String token, @RequestBody CommentDTO.CommentUpdateRequest request){
        if(!jwtUtil.validateJwt(token)){
            return null;
        }
        Comment comment = commentService.updateComment(request.getCommentId(), token, request.getContent());
        if(comment == null) return null;
        return ResponseEntity.status(HttpStatus.OK).body(new CommentDTO.CommentResponse(comment));
    }

    @GetMapping("/comment/article/{id}")
    public ResponseEntity <List<CommentDTO.CommentResponse>> articleComment(@PathVariable("id") Long articleId){
        List<CommentDTO.CommentResponse> response = new ArrayList<>();
        for(Comment comment : commentService.articleToComment(articleId)){
            response.add(new CommentDTO.CommentResponse(comment));
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@RequestHeader("Authorization") String token, @PathVariable("commentId") Long commentId){
        jwtUtil.validateJwt(token); //exception(?)
        commentService.deleteComment(commentId, token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
