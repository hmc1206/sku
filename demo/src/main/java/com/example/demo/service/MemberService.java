package com.example.demo.service;
//실제 비즈니스 로직 처리

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.utils.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //데이터베이스에서 하나의 작업 단위를 의미, 모든 작업을 하나로 묶음
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

    //모든 회원 정보를 리스트형식으로 가져옴
    public List<Member> findAll() {return memberRepository.findAll();}

    //memberId를 받아서 그값에 맞는 회원의 id를 찾음
    public Member findById(Long memberId) {return memberRepository.findById(memberId);}

    @Transactional
    //id,newName,newPassword를 매개변수로 받음
    public void update(Long id, String newName, String newPassword){

        //DB에서 id에 맞는 회원을 찾아서 member객체로 읽어오기
        Member member = memberRepository.findById(id);

        //member객채에서 username을 newName으로 변경
        member.setUsername(newName);

        //newPassword가 null,empty인지 확인
        if(newPassword != null && !newPassword.isEmpty()){
            //보안 토큰 작성
            String hashePassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            //member객체에서 password에 새로운 password로 변경
            member.setPassword(hashePassword);
        }

        //DB에 새 값으로 생성된 member의 정보를 저장
        memberRepository.save(member);
    }

    @Transactional
    public Long signUp(Member member){
        //BCrypt.gensalt : 암호활때마다 매번 다른 무작위값 생성
        //BCrypt.hashpw : member.getPassowr()값과 BCrypt.gensalt()의 값을 석어서 암호문 생성
        String hashedPassword = BCrypt.hashpw(member.getPassword(), BCrypt.gensalt());
        //memder의 password에 암호문 저장
        member.setPassword(hashedPassword);
        //DB에 member의 값을 저장
        memberRepository.save(member);

        //member의 id를 반환함
        return member.getId();
    }


    @Transactional
    //memberRepository(=>DB)에서 매개변수로 받은 id에 맞는 회원정보를 삭제
    public void delete(Long id) { memberRepository.remove(id);}

    public String login(String userId, String password){
        //memberRepository에서 userid에 맞는 회원을 찾아서 member객체로 읽음
        Member member = memberRepository.findByUserId(userId);

        //member의 값이 null이 아니거나 BCrypt.checkpw(password, member.getPassword())의 값이 0이 아닌지 확인
        if(member != null && BCrypt.checkpw(password, member.getPassword())){
            //JWT 토큰 발급
            String token = jwtUtil.generateJwt(member.getUserId(), member.getUsername());
            //토큰 반환
            return token;
        }

        //아이디나 비밀번호가 맞지 않으면 반환
        return "아이디와 비밀번호를 확인하세요";
    }

    //?
    public  Member findByUserId(String userId) {return memberRepository.findByUserId(userId);}
}
