package com.example.demo.domain;
//데이터를 표현하는 객체

import com.example.demo.enums.RoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//실제 값
@Setter
@Getter
@NoArgsConstructor
@Entity
public class Member {
    //DB에 자동으로 생성되는 순번(?)
    @Id
    @GeneratedValue
    private Long id;
    //userId => ex)hong01
    @Column(unique = true)
    private String userId;
    //password => ex)1234
    private String password;
    //username => ex)"홍길동";
    private String username;

    @Enumerated(EnumType.STRING)
    private RoleType roleType = RoleType.MEMBER;

    //member 클래스의 사용자 정의 생성자(초기화)
    public Member(String userId, String password, String username){
        this.userId = userId;
        this.password = password;
        this.username = username;
    }
}
