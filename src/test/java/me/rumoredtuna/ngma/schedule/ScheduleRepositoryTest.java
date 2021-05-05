package me.rumoredtuna.ngma.schedule;

import me.rumoredtuna.ngma.account.Account;
import me.rumoredtuna.ngma.account.AccountDto;
import me.rumoredtuna.ngma.account.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void findAllByOwner() {
        mockSchedule();
        List<Schedule> schedules = scheduleRepository.findAllByOwner(1L);

        assertThat(schedules.size()).isEqualTo(1);
        assertThat(schedules.get(0).getTitle()).isEqualTo("test");
    }

    public void mockSchedule() {
        AccountDto accountDto = new AccountDto();
        accountDto.setEmail("jilee@example.com");
        accountDto.setPassword("jilee");
        Account account = Account.from(accountDto);
        accountRepository.save(account);

        Schedule schedule = new Schedule();
        schedule.setTitle("test");
        schedule.setOwner(account);
        scheduleRepository.save(schedule);
    }
}