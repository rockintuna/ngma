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

    @GetMapping("/schedules")
    @ResponseBody
    public List<Schedule> getSchedules(@AuthenticationPrincipal UserAccount userAccount) {
        List<Schedule> schedules = scheduleService.getSchedules(userAccount);
        return schedules;
    }

    @PostMapping("/schedule")
    @ResponseBody
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleDto scheduleDto,
                                            @AuthenticationPrincipal UserAccount userAccount) {
        scheduleService.createSchedule(scheduleDto, userAccount);
        return ResponseEntity.ok("{}");
    }

    @PostMapping("/schedule/modify")
    @ResponseBody
    public ResponseEntity<?> modifySchedule(@RequestBody ScheduleDto scheduleDto) {
        scheduleService.modifySchedule(scheduleDto.getId(), scheduleDto);
        return ResponseEntity.ok("{}");
    }

    @DeleteMapping("/schedule")
    @ResponseBody
    public ResponseEntity<?> deleteSchedule(@RequestBody Long[] scheduleIdList) {
        for (Long id : scheduleIdList) {
            scheduleService.deleteSchedule(id);
        }
        return ResponseEntity.ok("{}");
    }
}
