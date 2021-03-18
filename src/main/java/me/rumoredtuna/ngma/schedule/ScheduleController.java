package me.rumoredtuna.ngma.schedule;

import me.rumoredtuna.ngma.account.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/{id}")
    @ResponseBody
    public Schedule getSchedule(@PathVariable Long id) {
        return scheduleService.getSchedule(id);
    }

    @GetMapping
    @ResponseBody
    public List<Schedule> getSchedules(@AuthenticationPrincipal UserAccount userAccount,
                                       @PageableDefault(size = 10) Pageable pageable) {
        List<Schedule> schedules = scheduleService.getSchedules(userAccount, pageable);
        return schedules;
    }

    @GetMapping("/page")
    @ResponseBody
    public int getSchedulePageInfo(@AuthenticationPrincipal UserAccount userAccount) {
        List<Schedule> schedules = scheduleService.getSchedules(userAccount);
        return schedules.size();
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleDto scheduleDto,
                                            @AuthenticationPrincipal UserAccount userAccount) {
        scheduleService.createSchedule(scheduleDto, userAccount);
        return ResponseEntity.ok("{}");
    }

    @PostMapping("/modify")
    @ResponseBody
    public ResponseEntity<?> modifySchedule(@RequestBody ScheduleDto scheduleDto) {
        scheduleService.modifySchedule(scheduleDto.getId(), scheduleDto);
        return ResponseEntity.ok("{}");
    }

    @DeleteMapping
    @ResponseBody
    public ResponseEntity<?> deleteSchedule(@RequestBody Long[] scheduleIdList) {
        for (Long id : scheduleIdList) {
            scheduleService.deleteSchedule(id);
        }
        return ResponseEntity.ok("{}");
    }
}
