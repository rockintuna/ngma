package me.rumoredtuna.ngma.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    @Query("select a.lover from Account a where a.id = :accountId")
    Account findLover(Long accountId);
}
