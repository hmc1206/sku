package com.example.demo.controller;

import com.example.demo.DTO.MemberDTO;
import com.example.demo.domain.Member;
import com.example.demo.service.MemberService;
import com.example.demo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/members")
    public MemberDTO.Result<Long> saveMember(@RequestBody MemberDTO.Request.Create request){
        Member member = new Member();
        member.setUserId(request.getUserId());
        member.setPassword(request.getPassword());
        member.setUsername(request.getUsername());


        //서비스 계층 호출하여 회원가입 처리
        Long id = memberService.signUp(member);

        return new MemberDTO.Result<>(id);
    }

    @PostMapping("/login")
    public MemberDTO.Result<String> login(@RequestBody MemberDTO.Request.Login request){
        String token = memberService.login(request.getUserId(), request.getPassword());
        return new MemberDTO.Result<>(token);
    }

    @GetMapping("/members")
    public MemberDTO.Result<List<MemberDTO.Response.Member>> findAllMembers(){
        List<Member> findMembers = memberService.findAll();

        List<MemberDTO.Response.Member> collect = findMembers.stream()
                .map(m -> new MemberDTO.Response.Member(m.getId(), m.getUserId(),m.getUsername()))
                .collect(Collectors.toList());

        return new MemberDTO.Result<>(collect);
    }

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
