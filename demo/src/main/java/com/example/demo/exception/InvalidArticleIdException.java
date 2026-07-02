package com.example.demo.exception;

public class InvalidArticleIdException extends RuntimeException {
    public InvalidArticleIdException(String message) {
        super(message);
    }
}
