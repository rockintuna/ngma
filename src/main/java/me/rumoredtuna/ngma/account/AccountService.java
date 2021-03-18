package me.rumoredtuna.ngma.account;

import me.rumoredtuna.ngma.config.exceptions.InvalidPasswordException;
import me.rumoredtuna.ngma.config.exceptions.PasswordWrongException;
import me.rumoredtuna.ngma.config.exceptions.PickMySelfException;
import me.rumoredtuna.ngma.config.exceptions.UsedEmailException;
import me.rumoredtuna.ngma.schedule.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        return new UserAccount(account);
    }

    public Account registerAccount(AccountDto accountDto) {
        checkPasswordIsNullOrShort(accountDto);

        if ( accountRepository.findByEmail(accountDto.getEmail()).isEmpty() ) {
            Account account = new Account(accountDto);
            account.setRole("USER");
            account.encodePassword(passwordEncoder);
            return accountRepository.save(account);
        } else {
            throw new UsedEmailException();
        }
    }

    private void checkPasswordIsNullOrShort(AccountDto accountDto) {
        if ( accountDto.getPassword() == null || accountDto.getPassword().length() < 8 ) {
            throw new InvalidPasswordException();
        }
    }

    public Account getUserById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(id.toString()));
    }

    public Account getUserByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    public void deleteAccount(UserAccount userAccount) {
        Account account = getUserById(userAccount.getAccountId());
        if ( account.getLoverState() == LoverState.COUPLED ) {
            account.getLover().setLover(null);
            account.getLover().setLoverState(LoverState.NOTHING);
        }
        clearWaiter(account);
        scheduleService.clearSchedule(account);
        accountRepository.deleteById(userAccount.getAccountId());
    }

    public void modifyAccount(UserAccount userAccount, AccountDto accountDto) {
        checkPasswordIsNullOrShort(accountDto);

        Account account = accountRepository.findByEmail(userAccount.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(userAccount.getUsername()));
        account.modifyByDto(accountDto);
        account.encodePassword(passwordEncoder);
        accountRepository.save(account);
    }

    public Account getLover(UserAccount userAccount) {
        return getUserById(userAccount.getAccountId()).getLover();
    }

    public void pickLover(UserAccount userAccount, String loverEmail) {
        if (userAccount.getAccount().getEmail().equals(loverEmail)) {
            throw new PickMySelfException();
        }
        Account account = getUserById(userAccount.getAccountId());
        Account lover = accountRepository.findByEmail(loverEmail)
                .orElseThrow(() -> new UsernameNotFoundException(loverEmail));
        account.setLover(lover);
        account.setLoverState(LoverState.WAITING);
        lover.getWaiters().add(account);
        lover.setLoverStateHasWaiters(true);
    }

    public List<Account> getWaiters(Account account) {
        return account.getWaiters();
    }

    public LoverState getLoverState(UserAccount userAccount) {
        Account account = getUserById(userAccount.getAccountId());
        if ( account.getWaiters().isEmpty() ) {
            account.getLoverState().setHasWaiters(false);
            return account.getLoverState();
        } else {
            account.getLoverState().setHasWaiters(true);
            return account.getLoverState();
        }
    }

    public void rejectWaiter(UserAccount userAccount, Long waiterId) {
        Account account = getUserById(userAccount.getAccountId());
        Account waiter = getUserById(waiterId);
        account.getWaiters().remove(waiter);
        waiter.setLover(null);
        waiter.setLoverState(LoverState.NOTHING);
    }

    public void confirmWaiter(UserAccount userAccount, Long waiterId) {
        Account account = getUserById(userAccount.getAccountId());
        Account waiter = getUserById(waiterId);
        account.setLover(waiter);
        account.setLoverState(LoverState.COUPLED);
        waiter.setLoverState(LoverState.COUPLED);
        account.getWaiters().remove(waiter);
        clearWaiter(account);
        clearWaiter(waiter);
    }

    public void clearWaiter(Account account) {
        List<Account> waiterList = account.getWaiters();
        for (Account waiter : waiterList) {
            waiter.setLover(null);
            waiter.setLoverState(LoverState.NOTHING);
        }
        waiterList.clear();
        account.setLoverStateHasWaiters(false);
    }

    public void cancelPick(UserAccount userAccount) {
        Account account = getUserById(userAccount.getAccountId());
        account.setLover(null);
        account.setLoverState(LoverState.NOTHING);
    }

    public void cancelLover(UserAccount userAccount) {
        Account account = getUserById(userAccount.getAccountId());
        Account lover = account.getLover();
        account.setLover(null);
        account.setLoverState(LoverState.NOTHING);
        lover.setLover(null);
        lover.setLoverState(LoverState.NOTHING);
    }
}
