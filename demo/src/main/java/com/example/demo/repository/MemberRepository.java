package com.example.demo.repository;

import com.example.demo.domain.Member;

import java.util.List;

//인터페이스(설계도)
public interface MemberRepository {
    //새로운 회원이나 기존 정보를 업데이트 함
    void save(Member member);
    //회원 한명을 찾아줌
    Member findById(Long id);
    //저장된 모든 회원 목록을 가져옴 => objectMapper.readValue()등
    List<Member> findAll();
    //고유 번호를 기준으로 회원을 삭제
    void remove(Long id);
    //로그인 아이디로 회원을 찾음
    Member findByUserId(String userId);
}
