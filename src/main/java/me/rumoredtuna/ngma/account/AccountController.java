package me.rumoredtuna.ngma.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.List;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public String register(@RequestBody AccountDto accountDto) {
        accountService.registerAccount(accountDto);
        return "redirect:/login";
    }

    @PostMapping("/update")
    public String modify(@RequestBody AccountDto accountDto,
                         @AuthenticationPrincipal UserAccount userAccount) {
        accountService.modifyAccount(userAccount, accountDto);
        return "redirect:/login";
    }

    @GetMapping("/loverState")
    @ResponseBody
    public LoverState getLoverState(@AuthenticationPrincipal UserAccount userAccount) {
        return accountService.getLoverState(userAccount);
    }

    @GetMapping("/lover")
    @ResponseBody
    public Account lover(@AuthenticationPrincipal UserAccount userAccount) {
        return accountService.getLover(userAccount);
    }

    @GetMapping("/waiter")
    @ResponseBody
    public List<Account> getWaiters(@AuthenticationPrincipal UserAccount userAccount) {
        return userAccount.getAccount().getWaiters();
    }

    @PostMapping("/pick")
    @ResponseBody
    public ResponseEntity<?> pick(@AuthenticationPrincipal UserAccount userAccount,
                                  @RequestBody AccountDto accountDto) {
        accountService.pickLover(userAccount, accountDto.getEmail());
        return ResponseEntity.ok().body("{}");
    }

    @PostMapping("/pick/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelPick(@AuthenticationPrincipal UserAccount userAccount) {
        accountService.cancelPick(userAccount);
        return ResponseEntity.ok().body("{}");
    }

    @PostMapping("/waiter/{id}")
    @ResponseBody
    public ResponseEntity<?> modifyWaiter(@AuthenticationPrincipal UserAccount userAccount,
                                    @PathVariable("id") Long id,@RequestParam String param) {
        if (param.equals("confirm")) {
            accountService.confirmWaiter(userAccount, id);
        } else if (param.equals("reject")) {
            accountService.rejectWaiter(userAccount, id);
        } else {
            throw new InvalidParameterException(param);
        }
        return ResponseEntity.ok().body("{}");
    }

}
