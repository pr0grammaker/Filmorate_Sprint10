//package ru.yandex.practicum.filmorate.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//
//import javax.swing.*;
//
//@RestControllerAdvice
//public class ErrorHandler {
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleNotFoundEx(final NotFoundException e) {
//        return new ErrorResponse("error", e.getMessage());
//    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleConditionNotMetException(final ConditionsNotMetException e) {
//        return new ErrorResponse("error", e.getMessage());
//    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleAnyOtherException(final Throwable throwable) {
//        return new ErrorResponse("error", throwable.getMessage());
//    }
//
//
//}
