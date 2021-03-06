package me.rumoredtuna.ngma.account;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager entityManager;



    @BeforeAll
    public void setup() {
        AccountDto accountDto = new AccountDto();
        accountDto.setPassword("jilee");
        accountDto.setEmail("jilee@example.com");
        Account account = Account.from(accountDto);
        accountRepository.save(account);
    }

    @Test
    public void test() {
        Account account = accountRepository.findByEmail("jilee@example.com").orElse(null);

        account.changeLoverState(LoverState.COUPLED);
        accountRepository.save(account);
        entityManager.flush();
        System.out.println(account.getLoverState());
    }
}