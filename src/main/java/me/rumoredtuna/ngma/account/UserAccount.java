package me.rumoredtuna.ngma.account;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

public class UserAccount extends User implements OAuth2User {

    private final Account account;

    private Map<String, Object> attributes;
    private String nameAttributeKey;

    public UserAccount(Map<String, Object> attributes, String nameAttributeKey, Account account) {
        super(account.getEmail(),"googleOauth2",
                List.of(new SimpleGrantedAuthority("ROLE_"+account.getRole())));
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.account = account;
    }

    public UserAccount(Account account) {
        super(account.getEmail(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_"+account.getRole())));
        this.account = account;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return this.attributes.get(this.nameAttributeKey).toString();
    }

    public Account getAccount() {
        return account;
    }

    public Long getAccountId() {
        return account.getId();
    }
}
