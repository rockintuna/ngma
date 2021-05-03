package me.rumoredtuna.ngma.account;

import lombok.Getter;

@Getter
public class SessionAccount {
    private String name;
    private String email;

    public SessionAccount(Account account) {
        this.name = account.getName();
        this.email = account.getEmail();
    }
}
