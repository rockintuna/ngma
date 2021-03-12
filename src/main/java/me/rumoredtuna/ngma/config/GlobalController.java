package me.rumoredtuna.ngma.config;

import me.rumoredtuna.ngma.config.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalController {

    @ExceptionHandler
    public ResponseEntity<?> usernameNotFoundException(UsernameNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("등록되지 않은 Email 주소입니다.");
    }

    @ExceptionHandler
    public ResponseEntity<?> usedEmailExceptionHandler(UsedEmailException exception) {
        return ResponseEntity.badRequest().body("can't create account, because of used email.");
    }

    @ExceptionHandler
    public ResponseEntity<?> invalidPasswordExceptionHandler(InvalidPasswordException exception) {
        return ResponseEntity.badRequest().body("can't create account, because of password.");
    }

    @ExceptionHandler
    public ResponseEntity<?> permissionDeniedExceptionHandler(PermissionDeniedException exception) {
        return ResponseEntity.badRequest().body("permission denied.");
    }

    @ExceptionHandler
    public ResponseEntity<?> passwordWrongException(PasswordWrongException exception) {
        return ResponseEntity.badRequest().body("wrong password");
    }

    @ExceptionHandler
    public ResponseEntity<?> pickMySelfException(PickMySelfException exception) {
        return ResponseEntity.badRequest().body("자신은 짝꿍이 될 수 없어요..");
    }
}
