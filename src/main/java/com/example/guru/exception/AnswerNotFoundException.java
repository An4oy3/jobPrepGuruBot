package com.example.guru.exception;

public class AnswerNotFoundException extends RuntimeException {

    public AnswerNotFoundException(String msg) {
        super(msg);
    }
}
