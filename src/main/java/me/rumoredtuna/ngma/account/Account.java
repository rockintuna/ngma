package me.rumoredtuna.ngma.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @NotNull
    private String email;

    @NotNull
    @JsonIgnore
    private String password;

    private String name;

    private String role;

    public Account() {
    }

    public Account(AccountDto accountDto) {
        this.email = accountDto.getEmail();
        this.password = accountDto.getPassword();
        this.name = accountDto.getName();
        this.loverState = LoverState.NOTHING;
    }

    @Override
    public String toString() {
        return "Account{" +
                ", email ='" + email + '\'' +
                '}';
    }

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "lover_id")
    private Account lover;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "waiter_id")
    @JsonIgnore
    private List<Account> waiters = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private LoverState loverState;

    public void modifyByDto(AccountDto accountDto) {
        this.password = accountDto.getPassword();
        this.name = accountDto.getName();
    }

    public void setLoverStateHasWaiters(boolean hasWaiters) {
        this.loverState.setHasWaiters(hasWaiters);
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    @JsonIgnore
    public String getAccessToken() {
        if (password == null) {
            return "";
        }
        return password.substring(0, 10);
    }
}
