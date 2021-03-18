package me.rumoredtuna.ngma.schedule;

import me.rumoredtuna.ngma.account.Account;
import me.rumoredtuna.ngma.account.AccountService;
import me.rumoredtuna.ngma.account.LoverState;
import me.rumoredtuna.ngma.account.UserAccount;
import me.rumoredtuna.ngma.config.exceptions.NotExistDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private AccountService accountService;

    public Schedule getSchedule(Long id) {
        return scheduleRepository.findById(id).orElseThrow(NotExistDataException::new);
    }

    public List<Schedule> getSchedules(UserAccount userAccount) {
        if ( accountService.getUserById(userAccount.getAccountId()).getLoverState()
                == LoverState.COUPLED) {
            return scheduleRepository.findAllByCouple(userAccount.getAccountId());
        } else {
            return scheduleRepository.findAllByOwner(userAccount.getAccountId());
        }
    }

    public List<Schedule> getSchedules(UserAccount userAccount, Pageable pageable) {
        if ( accountService.getUserById(userAccount.getAccountId()).getLoverState()
                == LoverState.COUPLED) {
            return scheduleRepository.findAllByCouple(userAccount.getAccountId(), pageable);
        } else {
            return scheduleRepository.findAllByOwner(userAccount.getAccountId(), pageable);
        }
    }

    public Schedule createSchedule(ScheduleDto scheduleDto, UserAccount userAccount) {
        Schedule schedule = new Schedule(scheduleDto);
        schedule.setOwner(accountService.getUserById(userAccount.getAccountId()));
        return scheduleRepository.save(schedule);
    }

    public void clearSchedule(Account account) {
        List<Schedule> scheduleList = scheduleRepository.findAllByOwner(account.getId());
        for (Schedule schedule : scheduleList) {
            scheduleRepository.delete(schedule);
        }
    }

    public Schedule modifySchedule(Long id, ScheduleDto scheduleDto) {
        Schedule schedule = getSchedule(id);
        schedule.modifyByDto(scheduleDto);
        return schedule;
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
}
