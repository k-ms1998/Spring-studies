package hello.exception.api;

import hello.exception.myException.BadRequestException;
import hello.exception.myException.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class ApiExceptionController {

    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {
        if (id.equals("ex")) {
            /**
             * {
             *     "timestamp": "2022-02-25T02:51:29.234+00:00",
             *     "status": 500,
             *     "error": "Internal Server Error",
             *     "path": "/api/members/ex"
             * }
             */
            throw new RuntimeException("잘못된 사용자 ApiExceptionController");
        } else if (id.equals("bad")) {
            /**
             * {
             *     "timestamp": "2022-02-28T03:19:49.421+00:00",
             *     "status": 400,
             *     "error": "Bad Request",
             *     "path": "/api/members/bad"
             * }
             */
            throw new IllegalArgumentException("잘못된 입력 값");
        } else if (id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }

        /**
         * @returns
         * {
         *     "memberId":id,
         *     "name": "hello"+id
         * }
         */
        return new MemberDto(id, "hello" + id);
    }

    @GetMapping("/api/response-status-ex1")
    public String responseStatusEx1() {
        /**
         * {
         *     "timestamp": "2022-02-28T08:57:34.049+00:00",
         *     "status": 400,
         *     "error": "Bad Request",
         *     "exception": "hello.exception.myException.BadRequestException",
         *     "message": "잘못된 요청 오류입니다. 메시지 사용",
         *     "path": "/api/response-status-ex1"
         * }
         */
        throw new BadRequestException();
    }

    @GetMapping("/api/response-status-ex2")
    public String responseStatusEx2() {
        /**
         * {
         *     "timestamp": "2022-03-02T02:06:55.166+00:00",
         *     "status": 404,
         *     "error": "Not Found",
         *     "exception": "org.springframework.web.server.ResponseStatusException",
         *     "message": "잘못된 요청 오류입니다. 메시지 사용",
         *     "path": "/api/response-status-ex2"
         * }
         */
//        ResponseStatusException(HttpStatus status, @Nullable String reason, @Nullable Throwable cause)
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "error.badRequest", new IllegalArgumentException());
    }


    @Data
    @AllArgsConstructor

    static class MemberDto {
        private String memberId;
        private String name;
    }
}