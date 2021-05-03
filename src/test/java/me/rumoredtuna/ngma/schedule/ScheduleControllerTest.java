package me.rumoredtuna.ngma.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rumoredtuna.ngma.account.*;
import me.rumoredtuna.ngma.config.exceptions.NotExistDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ScheduleControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    private void setUp() {

        AccountDto account = new AccountDto();
        account.setEmail("jilee@example.com");
        account.setPassword("jilee123");
        Account newAccount = accountService.registerAccount(account);
        UserAccount userAccount = new UserAccount(newAccount);

        AccountDto account2 = new AccountDto();
        account2.setEmail("sjlee@example.com");
        account2.setPassword("sjlee123");
        Account lover = accountService.registerAccount(account2);

        newAccount.setLover(lover);
        lover.setLover(newAccount);
        newAccount.setLoverState(LoverState.COUPLED);
        lover.setLoverState(LoverState.COUPLED);

        Schedule schedule1 = new Schedule();
        schedule1.setTitle("book");
        schedule1.setOwner(newAccount);
        scheduleRepository.save(schedule1);

        Schedule schedule2 = new Schedule();
        schedule2.setTitle("cafe");
        schedule2.setOwner(newAccount);
        scheduleRepository.save(schedule2);
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getSchedules() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/schedule")
                .param("page","0")
                .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("cafe")));
    }

    @Test
    @WithUserDetails(value = "sjlee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getOurSchedules() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/schedule"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("cafe")));
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void createSchedule() throws Exception {

        ScheduleDto scheduleDto = new ScheduleDto();
        scheduleDto.setTitle("meeting");
        String scheduleDtoString = objectMapper.writeValueAsString(scheduleDto);

        mvc.perform(post("/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(scheduleDtoString))
                .andDo(print())
                .andExpect(status().isOk());

        int size = scheduleRepository.findAll().size();
        Long id = scheduleRepository.findAll().get(size-1).getId();

        assertThat(scheduleService.getSchedule(id).getTitle()).isEqualTo("meeting");
    }

    @Test
    public void createScheduleWithoutAuth() throws Exception {
        mvc.perform(post("/schedule")
                .param("title", "cook"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void modifySchedule() throws Exception {
        ScheduleDto scheduleDto = new ScheduleDto();
        scheduleDto.setId(1l);
        scheduleDto.setTitle("meeting");
        String scheduleDtoString = objectMapper.writeValueAsString(scheduleDto);

        mvc.perform(post("/schedule/modify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(scheduleDtoString))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(scheduleService.getSchedule(1l).getTitle()).isEqualTo("meeting");
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void deleteSchedule() throws Exception {

        Long id = scheduleRepository.findAll().get(0).getId();

        Long[] scheduleIdList = new Long[1];
        scheduleIdList[0] = id;

        mvc.perform(delete("/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Arrays.toString(scheduleIdList)))
                .andDo(print())
                .andExpect(status().isOk());

        assertThrows(NotExistDataException.class, () -> scheduleService.getSchedule(id));
    }
}