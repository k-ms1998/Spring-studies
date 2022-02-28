package hello.exception.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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


    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}