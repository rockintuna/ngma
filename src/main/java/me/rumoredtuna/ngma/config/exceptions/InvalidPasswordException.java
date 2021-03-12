package me.rumoredtuna.ngma.config.exceptions;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException() {
        super("비밀번호를 제대로 입력해 주세요.");
    }
}
