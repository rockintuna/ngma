package me.rumoredtuna.ngma.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rumoredtuna.ngma.config.exceptions.InvalidPasswordException;
import me.rumoredtuna.ngma.config.exceptions.UsedEmailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    private void setUp() {
        AccountDto account1 = new AccountDto();
        account1.setEmail("jilee@example.com");
        account1.setPassword("jilee123");
        accountService.registerAccount(account1);

        AccountDto account2 = new AccountDto();
        account2.setEmail("sjlee@example.com");
        account2.setPassword("sjlee123");
        accountService.registerAccount(account2);
    }

    @Test
    public void registerAccount() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setEmail("nobody@example.com");
        accountDto.setPassword("newAccount");
        String accountDtoJson = objectMapper.writeValueAsString(accountDto);

        mvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(accountDtoJson))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        assertThat(accountService.getUserByEmail("nobody@example.com")).isNotNull();
    }

    @Test
    public void registerAccountWithShortPassword() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setEmail("nobody@example.com");
        accountDto.setPassword("short");
        String accountDtoJson = objectMapper.writeValueAsString(accountDto);

        mvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(accountDtoJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("패스워드는 8자 이상이어야 합니다."))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidPasswordException));
    }

    @Test
    public void registerAccountWithoutPassword() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setEmail("nobody@example.com");
        String accountDtoJson = objectMapper.writeValueAsString(accountDto);

        mvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(accountDtoJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("패스워드는 8자 이상이어야 합니다."))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidPasswordException));
    }

    @Test
    public void registerAccountExistEmail() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setEmail("jilee@example.com");
        accountDto.setPassword("password");
        String accountDtoJson = objectMapper.writeValueAsString(accountDto);

        mvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(accountDtoJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("사용중인 메일 주소 입니다."))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UsedEmailException));

        assertThat(accountService.getUserByEmail("jilee@example.com")).isNotNull();
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void modifyAccount() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setName("jileee");
        accountDto.setPassword("jilee321");
        String accountDtoJson = objectMapper.writeValueAsString(accountDto);

        mvc.perform(post("/account/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(accountDtoJson))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        assertThat(accountService.getUserByEmail("jilee@example.com").getName())
                .isEqualTo("jileee");
        assertThat(accountService.getUserByEmail("jilee@example.com").getPassword())
                .isNotNull();
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void deleteAccount() throws Exception {
        mvc.perform(delete("/account/delete"))
                .andDo(print())
                .andExpect(status().isOk());

        assertThrows(UsernameNotFoundException.class, () -> {
            accountService.getUserByEmail("jilee@example.com");
        });
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void showLover() throws Exception {
        Account account = accountService.getUserByEmail("jilee@example.com");
        Account lover = accountService.getUserByEmail("sjlee@example.com");
        account.changeLover(lover);
        account.changeLoverState(LoverState.WAITING);
        lover.getWaiters().add(account);
        lover.setLoverStateHasWaiters(true);

        mvc.perform(MockMvcRequestBuilders.get("/account/lover"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("sjlee@example.com")));
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void showCouple() throws Exception {
        Account account = accountService.getUserByEmail("jilee@example.com");
        Account lover = accountService.getUserByEmail("sjlee@example.com");
        account.changeLover(lover);
        lover.changeLover(account);
        account.changeLoverState(LoverState.COUPLED);
        lover.changeLoverState(LoverState.COUPLED);

        mvc.perform(MockMvcRequestBuilders.get("/account/lover"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("sjlee@example.com")));
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void showNoCouple() throws Exception {
        Account account = accountService.getUserByEmail("jilee@example.com");
        Account lover = accountService.getUserByEmail("sjlee@example.com");

        mvc.perform(MockMvcRequestBuilders.get("/account/lover"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getLoverStateNoWaiterNothing() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/account/loverState"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("NOTHING"))
                .andExpect(jsonPath("$.hasWaiters").value(false));
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getLoverStateNoWaiterWaiting() throws Exception {
        Account account = accountService.getUserByEmail("jilee@example.com");
        Account lover = accountService.getUserByEmail("sjlee@example.com");
        account.changeLover(lover);
        account.changeLoverState(LoverState.WAITING);
        lover.getWaiters().add(account);
        lover.setLoverStateHasWaiters(true);

        mvc.perform(MockMvcRequestBuilders.get("/account/loverState"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("WAITING"))
                .andExpect(jsonPath("$.hasWaiters").value(false));
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getLoverStateHasWaiterNothing() throws Exception {
        Account account = accountService.getUserByEmail("jilee@example.com");
        Account lover = accountService.getUserByEmail("sjlee@example.com");
        lover.changeLover(account);
        lover.changeLoverState(LoverState.WAITING);
        account.getWaiters().add(lover);
        account.setLoverStateHasWaiters(true);

        mvc.perform(MockMvcRequestBuilders.get("/account/loverState"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("NOTHING"))
                .andExpect(jsonPath("$.hasWaiters").value(true));
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getLoverStateHasWaiterWaiting() throws Exception {
        Account account = accountService.getUserByEmail("jilee@example.com");
        Account lover = accountService.getUserByEmail("sjlee@example.com");
        account.changeLover(lover);
        account.changeLoverState(LoverState.WAITING);
        lover.getWaiters().add(account);
        lover.setLoverStateHasWaiters(true);
        lover.changeLover(account);
        lover.changeLoverState(LoverState.WAITING);
        account.getWaiters().add(lover);
        account.setLoverStateHasWaiters(true);

        mvc.perform(MockMvcRequestBuilders.get("/account/loverState"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("WAITING"))
                .andExpect(jsonPath("$.hasWaiters").value(true));
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getLoverStateCoupled() throws Exception {
        Account account = accountService.getUserByEmail("jilee@example.com");
        Account lover = accountService.getUserByEmail("sjlee@example.com");
        account.changeLover(lover);
        lover.changeLover(account);
        account.changeLoverState(LoverState.COUPLED);
        lover.changeLoverState(LoverState.COUPLED);

        mvc.perform(MockMvcRequestBuilders.get("/account/loverState"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("COUPLED"))
                .andExpect(jsonPath("$.hasWaiters").value(false));
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void pick() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setEmail("sjlee@example.com");
        String accountDtoJson = objectMapper.writeValueAsString(accountDto);

        mvc.perform(post("/account/pick")
                .contentType(MediaType.APPLICATION_JSON)
                .content(accountDtoJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void pickNotExistAccount() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setEmail("sjlee123@example.com");
        String accountDtoJson = objectMapper.writeValueAsString(accountDto);

        mvc.perform(post("/account/pick")
                .contentType(MediaType.APPLICATION_JSON)
                .content(accountDtoJson))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("등록되지 않은 Email 주소입니다."));
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void pickMySelf() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setEmail("jilee@example.com");
        String accountDtoJson = objectMapper.writeValueAsString(accountDto);

        mvc.perform(post("/account/pick")
                .contentType(MediaType.APPLICATION_JSON)
                .content(accountDtoJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("자신은 짝꿍이 될 수 없어요.."));
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void cancelpick() throws Exception {
        Account account = accountService.getUserByEmail("jilee@example.com");
        Account lover = accountService.getUserByEmail("sjlee@example.com");
        lover.changeLover(account);
        lover.changeLoverState(LoverState.WAITING);
        account.getWaiters().add(lover);
        account.setLoverStateHasWaiters(true);

        mvc.perform(post("/account/pick/cancel"))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(account.getLover()).isNull();
        assertThat(account.getLoverState()).isEqualTo(LoverState.NOTHING);
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void confirmWaiter() throws Exception {
        Account lover = accountService.getUserByEmail("sjlee@example.com");
        Account account = accountService.getUserByEmail("jilee@example.com");
        lover.changeLover(account);
        lover.changeLoverState(LoverState.WAITING);
        account.getWaiters().add(lover);
        account.setLoverStateHasWaiters(true);

        assertThat(lover.getLoverState()).isEqualTo(LoverState.WAITING);
        assertThat(account.getLoverState().getHasWaiters()).isEqualTo(true);
        assertThat(account.getWaiters()).isNotEmpty();

        mvc.perform(post("/account/waiter/" + lover.getId())
                .param("param", "confirm"))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(lover.getLoverState()).isEqualTo(LoverState.COUPLED);
        assertThat(account.getLoverState()).isEqualTo(LoverState.COUPLED);
        assertThat(account.getLoverState().getHasWaiters()).isEqualTo(false);
        assertThat(account.getWaiters()).isEmpty();
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void rejectWaiter() throws Exception {
        Account lover = accountService.getUserByEmail("sjlee@example.com");
        Account account = accountService.getUserByEmail("jilee@example.com");
        lover.changeLover(account);
        lover.changeLoverState(LoverState.WAITING);
        account.getWaiters().add(lover);
        account.setLoverStateHasWaiters(true);

        assertThat(lover.getLoverState()).isEqualTo(LoverState.WAITING);
        assertThat(account.getLoverState().getHasWaiters()).isEqualTo(true);
        assertThat(account.getWaiters().contains(lover)).isTrue();
        assertThat(account.getWaiters()).isNotEmpty();

        mvc.perform(post("/account/waiter/" + lover.getId())
                .param("param", "reject"))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(lover.getLoverState()).isEqualTo(LoverState.NOTHING);
        assertThat(account.getWaiters().contains(lover)).isFalse();
    }

    @Test
    @WithUserDetails(value = "jilee@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void cancelLover() throws Exception {
        Account account = accountService.getUserByEmail("jilee@example.com");
        Account lover = accountService.getUserByEmail("sjlee@example.com");
        account.changeLover(lover);
        lover.changeLover(account);
        account.changeLoverState(LoverState.COUPLED);
        lover.changeLoverState(LoverState.COUPLED);

        mvc.perform(post("/account/lover/cancel"))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(lover.getLoverState()).isEqualTo(LoverState.NOTHING);
        assertThat(account.getLoverState()).isEqualTo(LoverState.NOTHING);
    }

}