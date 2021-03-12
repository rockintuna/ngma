package me.rumoredtuna.ngma.schedule;

import me.rumoredtuna.ngma.account.Account;
import me.rumoredtuna.ngma.account.LoverState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Schedule> getSchedules(Account account) {
        if ( account.getLoverState() == LoverState.COUPLED) {
            return scheduleRepository.findAllByCouple(account.getId());
        } else {
            return scheduleRepository.findAllByOwner(account.getId());
        }
    }

//    public List<Schedule> getOurSchedules(Long accountId) {
//        return scheduleRepository.findAllByCouple(accountId);
//    }

    public Schedule createSchedule(Schedule schedule, Account account) {
        schedule.setOwner(account);
        return scheduleRepository.save(schedule);
    }
}
