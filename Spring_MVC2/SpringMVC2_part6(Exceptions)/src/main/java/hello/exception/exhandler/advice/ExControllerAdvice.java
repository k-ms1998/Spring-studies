package hello.exception.exhandler.advice;

import hello.exception.exhandler.ErrorResult;
import hello.exception.myException.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST) //Status Code 400 전달; 생략시 200 전달
    @ExceptionHandler(IllegalArgumentException.class)
    //IllegalArgumentException 에러 발생시 catch해서 메서드 호출; 현재 컨트롤러에서 발생하는 에러에 대해서만 처리
    public ErrorResult illegalExHandler(IllegalArgumentException e) {
        log.error("[exceptionHandler] ex", e);

        return new ErrorResult("BAD", e.getMessage());
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResult> userExHandler(UserException e) {
        log.error("[exceptionHandler] ex", e);

        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());


//        @ResponseStatus 대신 return ResponseEntity 를 통해 Status Code 전달 가능
        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //Status Code 500
    @ExceptionHandler   // 위의 IllegalArgumentException, UserException 외의 exception 처리
    public ErrorResult exHandler(Exception e) {
        log.error("[exceptionHandler] ex", e);

        return new ErrorResult("EX", "내부 오류");
    }
}
