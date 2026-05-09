package com.example.demo.service;
//실제 비즈니스 로직 처리

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    //토큰을 확인해서 현재 이용중인 회원 정보를 DB에서 찾아옴
    public Member tokenToMember(String token){ //token을 매개변수로 받음
        //userId를 반환해줌
        return memberRepository.findByUserId(jwtUtil.getClaimsFromJwt(token).getSubject());
        //jwtUtil.getClaimsFromJwt(token) : 토큰을 해독
        //.getSubject() : 해독한 내용에서 ID를 가져옴
        //memberRepository.findByUserId() : 아이디로 DB에 가서 회원 객체를 가져옴(member)
    }

    public List<Member> findAll() {return memberRepository.findAll();}

    public Member findById(Long memberId) {return memberRepository.findById(memberId);}

    public void update(Long id, String newName, String newPassword){
        Member member = memberRepository.findById(id);

        member.setUsername(newName);
        if(newPassword != null && !newPassword.isEmpty()){
            String hashePassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            member.setPassword(hashePassword);
        }

        memberRepository.save(member);
    }

    public Long signUp(Member member){
        String hashedPassword = BCrypt.hashpw(member.getPassword(), BCrypt.gensalt());
        member.setPassword(hashedPassword);
        memberRepository.save(member);
        return member.getId();
    }

    public void delete(Long id) { memberRepository.remove(id);}

    public String login(String userId, String password){
        Member member = memberRepository.findByUserId(userId);

        if(member != null && BCrypt.checkpw(password, member.getPassword())){
            String token = jwtUtil.generateJwt(member.getUserId(), member.getUsername());
            return token;
        }

        return "아이디와 비밀번호를 확인하세요";
    }

    public  Member findByUserId(String userId) {return memberRepository.findByUserId(userId);}
}
