package com.example.demo.repository;

import com.example.demo.domain.Member;

import java.util.List;

public interface MemberRepository {
    void save(Member member);
    Member findById(Long id);
    List<Member> findAll();
    void remove(Long id);
    Member findByUserId(String userId);
}
