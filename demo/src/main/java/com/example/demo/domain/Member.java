package com.example.demo.domain;
//데이터를 표현하는 객체

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//실제 값
@Setter
@Getter
@NoArgsConstructor
public class Member {
    //DB에 자동으로 생성되는 순번(?)
    private Long id;
    //userId => ex)hong01
    private String userId;
    //password => ex)1234
    private String password;
    //username => ex)"홍길동";
    private String username;

    //member 클래스의 사용자 정의 생성자(초기화)
    public Member(String userId, String password, String username){
        this.userId = userId;
        this.password = password;
        this.username = username;
    }
}
