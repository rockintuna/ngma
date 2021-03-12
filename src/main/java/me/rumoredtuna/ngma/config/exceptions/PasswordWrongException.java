package me.rumoredtuna.ngma.config.exceptions;

public class PasswordWrongException extends RuntimeException{
    public PasswordWrongException() {
        super("패스워드가 일치하지 않습니다.");
    }
}
