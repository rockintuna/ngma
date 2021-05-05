package me.rumoredtuna.ngma.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @NotNull
    private String email;

    @JsonIgnore
    private String password;

    private String name;

    private String role;

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

    @Builder
    private Account(Long id, String email, String password, String name, String role, Account lover, List<Account> waiters, LoverState loverState) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.lover = lover;
        this.waiters = waiters;
        this.loverState = loverState;
    }

    public static Account from(AccountDto accountDto) {
        return Account.builder()
                .email(accountDto.getEmail())
                .password(accountDto.getPassword())
                .name(accountDto.getName())
                .role("USER")
                .loverState(LoverState.NOTHING)
                .build();
    }

    public static Account ofGoogle(Map<String, Object> attributes) {
        return Account.builder()
                .email(attributes.get("email").toString())
                .name(attributes.get("name").toString())
                .role("USER")
                .loverState(LoverState.NOTHING)
                .build();
    }

    public void changeLover(Account lover) {
        this.lover = lover;
    }

    public void changeLoverState(LoverState loverState) {
        this.loverState = loverState;
    }

    public void deregisterLover() {
        changeLover(null);
        changeLoverState(LoverState.NOTHING);
    }

    public void waitingFor(Account lover) {
        changeLover(lover);
        changeLoverState(LoverState.WAITING);
    }

    public void clearWaiters() {
        List<Account> waiterList = getWaiters();
        for (Account waiter : waiterList) {
            waiter.deregisterLover();
        }
        waiterList.clear();
        setLoverStateHasWaiters(false);
    }

    @Override
    public String toString() {
        return "Account{" +
                ", email ='" + email + '\'' +
                '}';
    }

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

}
