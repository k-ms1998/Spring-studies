package hello.exception.api;

import hello.exception.exhandler.ErrorResult;
import hello.exception.myException.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api2")
public class ApiExceptionV2Controller {

    @GetMapping("/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {
        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자 ApiExceptionController");
        } else if (id.equals("bad")) {
            /**
             * {
             *     "code": "BAD",
             *     "message": "잘못된 입력 값"
             * }
             *
             * IllegalArgumentException()
             * => public ErrorResult illegalExHandler(IllegalArgumentException e) 호출
             */
            throw new IllegalArgumentException("잘못된 입력 값");
        } else if (id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello" + id);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
