package me.rumoredtuna.ngma.config.exceptions;

public class UsedEmailException extends RuntimeException {
    public UsedEmailException() {
        super("이미 사용중인 메일 주소 입니다.");
    }
}
