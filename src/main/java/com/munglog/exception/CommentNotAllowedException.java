package com.munglog.exception;

public class CommentNotAllowedException extends RuntimeException {

    public CommentNotAllowedException(String message) {
        super(message);
    }
}
