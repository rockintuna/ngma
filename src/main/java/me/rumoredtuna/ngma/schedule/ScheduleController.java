package me.rumoredtuna.ngma.schedule;

import me.rumoredtuna.ngma.account.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ResponseBody
    @GetMapping("/schedules")
    public List<Schedule> getSchedules(@AuthenticationPrincipal UserAccount userAccount) {
        List<Schedule> schedules = scheduleService.getSchedules(userAccount.getAccount());
        return schedules;
    }

    @ResponseBody
    @PostMapping("/schedule")
    public ResponseEntity<?> createSchedulexSubmit(@ModelAttribute Schedule schedule,
                                       @AuthenticationPrincipal UserAccount userAccount) {
        scheduleService.createSchedule(schedule, userAccount.getAccount());
        return ResponseEntity.ok().build();
    }

}
