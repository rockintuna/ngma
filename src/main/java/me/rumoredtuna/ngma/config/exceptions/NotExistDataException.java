package me.rumoredtuna.ngma.config.exceptions;

public class NotExistDataException extends RuntimeException{
    public NotExistDataException() {
        super("데이터가 존재하지 않습니다.");
    }
}
