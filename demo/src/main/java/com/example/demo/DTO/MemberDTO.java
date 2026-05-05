package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MemberDTO {
    private MemberDTO(){

    }

    public static class Request {
        @Data
        @NoArgsConstructor
        public static class Create {
            private String userId;
            private String password;
            private String username;
        }

        @Data
        @NoArgsConstructor
        public static class Update{
            private String username;
            private String password;
        }

        @Data
        @NoArgsConstructor
        public static class Login {
            private String userId;
            private String password;
        }
    }

    public static class Response{
        @Data
        @AllArgsConstructor
        public static class Member {
            private Long id;
            private String userId;
            private String username;
        }
    }

    @Data
    @AllArgsConstructor
    public static class Result<T> {
        private T data;
    }
}
