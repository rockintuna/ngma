package me.rumoredtuna.ngma.account;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class AccountDto {

    private Long id;

    private String email;

    private String password;

    private String role;

    private String name;

}
