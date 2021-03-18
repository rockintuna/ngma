package me.rumoredtuna.ngma.config;

import me.rumoredtuna.ngma.account.Account;
import me.rumoredtuna.ngma.account.AccountDto;
import me.rumoredtuna.ngma.account.AccountService;
import me.rumoredtuna.ngma.account.UserAccount;
import me.rumoredtuna.ngma.schedule.Schedule;
import me.rumoredtuna.ngma.schedule.ScheduleRepository;
import me.rumoredtuna.ngma.schedule.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Profile("!test")
@Component
@Transactional
public class AfterInit implements ApplicationRunner {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {


        AccountDto account1 = new AccountDto();
        account1.setEmail("jilee@example.com");
        account1.setPassword("jilee321");
        account1.setName("이정인");
        Account account = accountService.registerAccount(account1);

        for (int i=1; i<100; i++) {
            Schedule schedule = new Schedule();
            schedule.setTitle("일정"+i);
            schedule.setDateTime(LocalDateTime.now());
            schedule.setPlace("강남");
            schedule.setOwner(account);
            schedule.setOwner(accountService.getUserById(account.getId()));
            scheduleRepository.save(schedule);
        }

        AccountDto account2 = new AccountDto();
        account2.setEmail("sjlee@example.com");
        account2.setPassword("sjlee123");
        account2.setName("이수진");
        accountService.registerAccount(account2);

    }
}
