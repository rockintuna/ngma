package me.rumoredtuna.ngma.config.exceptions;

public class PickMySelfException extends RuntimeException{
    public PickMySelfException() {
        super("자신은 짝꿍이 될 수 없습니다.");
    }
}
