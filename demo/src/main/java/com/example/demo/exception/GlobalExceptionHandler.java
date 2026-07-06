package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidArticleIdException.class)
    public ResponseEntity<String> handleInvalidArticleId(InvalidArticleIdException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 articleId");
    }

    @ExceptionHandler(InvalidArticleIdException.class)
    public ResponseEntity<String> handleInvalidComment(InvalidArticleIdException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
