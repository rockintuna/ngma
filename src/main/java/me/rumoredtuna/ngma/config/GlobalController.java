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
        return ResponseEntity.badRequest().body("사용중인 메일 주소 입니다.");
    }

    @ExceptionHandler
    public ResponseEntity<?> invalidPasswordExceptionHandler(InvalidPasswordException exception) {
        return ResponseEntity.badRequest().body("패스워드는 8자 이상이어야 합니다.");
    }

    @ExceptionHandler
    public ResponseEntity<?> permissionDeniedExceptionHandler(PermissionDeniedException exception) {
        return ResponseEntity.badRequest().body("permission denied.");
    }

    @ExceptionHandler
    public ResponseEntity<?> passwordWrongException(PasswordWrongException exception) {
        return ResponseEntity.badRequest().body("잘못된 패스워드 입니다.");
    }

    @ExceptionHandler
    public ResponseEntity<?> pickMySelfException(PickMySelfException exception) {
        return ResponseEntity.badRequest().body("자신은 짝꿍이 될 수 없어요..");
    }

    @ExceptionHandler
    public ResponseEntity<?> notExistDataException(NotExistDataException exception) {
        return ResponseEntity.badRequest().body("데이터가 존재하지 않습니다.");
    }
}
