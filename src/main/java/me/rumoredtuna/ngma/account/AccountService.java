package me.rumoredtuna.ngma.account;

import me.rumoredtuna.ngma.config.exceptions.InvalidPasswordException;
import me.rumoredtuna.ngma.config.exceptions.PickMySelfException;
import me.rumoredtuna.ngma.config.exceptions.UsedEmailException;
import me.rumoredtuna.ngma.schedule.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AccountService implements UserDetailsService, OAuth2UserService<OAuth2UserRequest, OAuth2User> {

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
            Account account = Account.from(accountDto);
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
        if ( account.getLoverState().equals(LoverState.COUPLED) ) {
            account.getLover().deregisterLover();
        }
        account.clearWaiters();
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
        account.waitingFor(lover);
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
        waiter.deregisterLover();
    }

    public void confirmWaiter(UserAccount userAccount, Long waiterId) {
        Account account = getUserById(userAccount.getAccountId());
        Account waiter = getUserById(waiterId);

        account.changeLover(waiter);
        account.changeLoverState(LoverState.COUPLED);
        waiter.changeLoverState(LoverState.COUPLED);

        account.getWaiters().remove(waiter);
        account.clearWaiters();
        waiter.clearWaiters();
    }

    public void cancelPick(UserAccount userAccount) {
        Account account = getUserById(userAccount.getAccountId());
        account.deregisterLover();
    }

    public void cancelLover(UserAccount userAccount) {
        Account account = getUserById(userAccount.getAccountId());
        Account lover = account.getLover();
        account.deregisterLover();
        lover.deregisterLover();
    }

    @Override
    public UserAccount loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        Map<String, Object> attributes = delegate.loadUser(oAuth2UserRequest).getAttributes();

        String userNameAttributeName = oAuth2UserRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        return new UserAccount(
                attributes
                , userNameAttributeName
                , getAccountByOAuthAttributes(attributes));
    }

    private Account getAccountByOAuthAttributes(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        Optional<Account> account = accountRepository.findByEmail(email);

        if ( account.isEmpty() ) {
            return accountRepository.save(Account.ofGoogle(attributes));
        } else {
            return account.get();
        }
    }
}
