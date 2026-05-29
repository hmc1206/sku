package com.example.demo.repository;

import com.example.demo.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaMemberRepository implements MemberRepository{

    private final EntityManager em;

    @Override
    public  void save(Member member) { em.persist(member);}

    @Override
    public Member findById(Long id){
        return em.find(Member.class, id);
    }

    @Override
    public void remove(Long id){
        Member member = findById(id);
        em.remove(member);
    }

    @Override
    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    @Override
    public Member findByUserId(String userId){
        return em.createQuery("select m from Member m where m.userId = :userId", Member.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }
}
