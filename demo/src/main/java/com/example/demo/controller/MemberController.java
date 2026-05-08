package com.example.demo.controller;
//사용자의 요청을 받아서 어떤 로직을 처리할지 결정

import com.example.demo.DTO.MemberDTO;
import com.example.demo.domain.Member;
import com.example.demo.service.MemberService;
import com.example.demo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

//클래스 레벨
@Slf4j
@RestController //json 반환 컨트롤러
@RequiredArgsConstructor //생성자 자동 생성
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    //private : 캡슐화(보안) => 해당 필드를 클래스 내부에서만 접근할 수 있도록 제한
    //final : 뷸변성, 초기화 필수
    //MemberService : 타입 => 주입받을 객체의 인터페이스 또는 클래스 타입을 지정함
    //memberService : 변수명

    //메서드 레벨
    @PostMapping("/members") // /api/members(post)
    public MemberDTO.Result<Long> saveMember(@RequestBody MemberDTO.Request.Create request){ //파라미터 레벨
        //RequestBody : 요청 Body에서 가져오기
        //MemberDTO.Request.Create request => MemberDTO 클래스 안에 Request 클래스 안에 있는 Create 클래스의 인스턴스를 생성해서 request라는 변수에 활당
        Member member = new Member(); //member라는 객체 생성
        //log.info("전체 요청 데이터: {}", request);
        //전체 요청 데이터: MemberDTO.Request.Create(userId=hong01, password=1234, username=홍길동)
        member.setUserId(request.getUserId());
        member.setPassword(request.getPassword());
        member.setUsername(request.getUsername());


        //서비스 계층 호출하여 회원가입 처리
        Long id = memberService.signUp(member);

        //프론트엔드에게 전달 값
        return new MemberDTO.Result<>(id);
    }

    //로그인
    @PostMapping("/login") //post요청시에 /api/login
    public MemberDTO.Result<String> login(@RequestBody MemberDTO.Request.Login request){
        //memberService.login() 메소드에 매개변수로 id와 password를 보내고 token값을 반환 받음
        String token = memberService.login(request.getUserId(), request.getPassword());

        //프론트엔드에게 token값을 전달
        return new MemberDTO.Result<>(token);
    }


    //회원 조회
    @GetMapping("/members")
    public MemberDTO.Result<List<MemberDTO.Response.Member>> findAllMembers(){
        //List<> 회원 한 명이 아닌 모든 회원 정보
        //MemberDTO.Response.Member => 회원정보에서 비밀번호를 뺀 값들

        //
        List<Member> findMembers = memberService.findAll();

        List<MemberDTO.Response.Member> collect = findMembers.stream()
                .map(m -> new MemberDTO.Response.Member(m.getId(), m.getUserId(),m.getUsername()))
                .collect(Collectors.toList());

        return new MemberDTO.Result<>(collect);
    }

    //회원 정보 갱신
    @PutMapping("/members")
    public MemberDTO.Result<?> updateMember(
            @RequestBody MemberDTO.Request.Update request,
            @RequestHeader("Authorization") String token) {
        
        if(!jwtUtil.validateJwt(token)){
            return new MemberDTO.Result<>("유효한 토큰이 아닙니다");
        }

        Long id = memberService.tokenToMember(token).getId();
        memberService.update(id, request.getUsername(), request.getPassword());

        Member findmember = memberService.findById(id);
        return new MemberDTO.Result<>(
                new MemberDTO.Response.Member(findmember.getId(), findmember.getUserId(), findmember.getUsername()));
    }

    //회원 삭제
    @DeleteMapping("/members")
    public MemberDTO.Result<String> deleteMember(@RequestHeader("Authorization") String token){
        if(!jwtUtil.validateJwt(token)){
            return new MemberDTO.Result<>("유효한 토큰이 아닙니다.");
        }

        Long id = memberService.tokenToMember(token).getId();
        memberService.delete(id);

        return new MemberDTO.Result<>("회원삭제 완료");
    }
}
