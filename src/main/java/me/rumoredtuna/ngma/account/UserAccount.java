package me.rumoredtuna.ngma.account;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class UserAccount extends User {

    private Account account;

    public Account getAccount() {
        return account;
    }

    public UserAccount(Account account) {
        super(account.getEmail(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_"+account.getRole())));
        this.account = account;
    }

    public Long getAccountId() {
        return account.getId();
    }
}
