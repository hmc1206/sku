package com.example.demo.DTO;
//데이터를 담는 전용 객체(Data Transfer Object)

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MemberDTO {
    //기본 생성자
    private MemberDTO(){

    }

    //요청 데이터 전용 DTO
    public static class Request {

        //생성을 요청받을때
        @Data
        @NoArgsConstructor
        public static class Create {
            private String userId;
            private String password;
            private String username;
        }

        //Update를 요청 받을때
        @Data
        @NoArgsConstructor
        public static class Update{
            private String username;
            private String password;
        }

        //login을 요청 받을때
        @Data
        @NoArgsConstructor
        public static class Login {
            private String userId;
            private String password;
        }
    }

    //응답 데이터 전용 DTO
    public static class Response{
        @Data
        @AllArgsConstructor
        public static class Member {
            private Long id;
            private String userId;
            private String username;
        }
    }

    //공통 응답 래퍼(Wrapper) <T> : 제네릭
    @Data
    @AllArgsConstructor
    public static class Result<T> {
        private T data;
    }
}
