package me.rumoredtuna.ngma.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rumoredtuna.ngma.account.Account;
import me.rumoredtuna.ngma.account.AccountDto;
import me.rumoredtuna.ngma.account.AccountService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
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
    private ObjectMapper objectMapper;

    @BeforeEach
    private void setUp() {

        AccountDto account = new AccountDto();
        account.setEmail("jilee@example.com");
        account.setPassword("jilee123");
        Account newAccount = accountService.registerAccount(account);

        Schedule schedule1 = new Schedule();
        schedule1.setTitle("book");

        Schedule schedule2 = new Schedule();
        schedule2.setTitle("cafe");

        scheduleService.createSchedule(schedule1, newAccount);
        scheduleService.createSchedule(schedule2, newAccount);
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getSchedules() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/schedules")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("cafe")));
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void createSchedule() throws Exception {

        mvc.perform(post("/schedule")
                .param("title", "cook"))
                .andDo(print())
                .andExpect(status().isOk());

        Account account = accountService.getUserByEmail("jilee@example.com");

        assertThat(scheduleService.getSchedules(account)
                .size()).isEqualTo(3);
        assertThat(scheduleService.getSchedules(account)
                .get(2).getTitle()).isEqualTo("cook");
    }

    @Test
    public void createScheduleWithoutAuth() throws Exception {
        mvc.perform(post("/schedule")
                .param("title", "cook"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}