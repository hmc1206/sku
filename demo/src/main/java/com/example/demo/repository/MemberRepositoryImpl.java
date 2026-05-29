package com.example.demo.repository;
//데이터베이스와 통신하여 데이터 저장/조회/수정/삭제 등 수행

import com.example.demo.domain.Member;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j // 콘솔에 상태를 찍기 위해 사용 (Lombok)
//@Repository // 스프링 빈으로 등록
public class MemberRepositoryImpl implements MemberRepository {

    // JSON 파일 읽기/쓰기용 Jackson 객체
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 회원 데이터를 저장할 파일 위치
    private final String DATA_FILE_PATH = "data/members.json";

    // 메모리 저장소 (실무에서는 DB가 대신함)
    private final Map<Long, Member> store = new ConcurrentHashMap<>();

    // ID 자동 증가 시퀀스
    private final AtomicLong sequence = new AtomicLong(0L);

    public MemberRepositoryImpl() { // 생성자
        loadDataFromFile(); // 앱 시작 시 파일 → 메모리 로드 (의존성 주입)
    }

    // 파일에 있던 회원 목록을 메모리로 불러오기
    private void loadDataFromFile() {
        File file = new File(DATA_FILE_PATH);
        if (file.exists()) {
            try {
                //파일에 있는 회원들의 정보 읽어서 list로 저장
                List<Member> members = objectMapper.readValue(file, new TypeReference<List<Member>>() {
                });
                //각각의 멤버의 회원들을 반복문으로~~
                for (Member member : members) {
                    //Map 형태의 저장소에 하나씩 저장
                    store.put(member.getId(), member);

                    //ID 중복 충동 방지
                    if (member.getId() > sequence.get()) {
                        sequence.set(member.getId());
                    }
                }
                //명몇의 회원 데이터를 읽었는지 알려줌
                log.info("회원 데이터 로드 완료: {}명", members.size());
            } catch (Exception e) {
                //오류가 뜰 때
                log.error("회원 데이터 로드 실패", e);
            }
        } else {
            //파일이 없으면 새로 만들기
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            log.info("기존 데이터 파일이 없어 새로 시작합니다.");
        }
    }

    // 메모리 내용을 JSON 파일에 저장 (DB대체)
    private void saveDataToFile() {
        try {
            //데이터를 저장하는 내용
            List<Member> members = new ArrayList<>(store.values());

            //writerWithDefaultPrettyPrinter() => json 값을 들여쓰기와 줄바꿈을 해줌
            //writeValue() => 1번째 인자 : 데이터를 저장할 파일, 2번째 인자 : json으로 변환할 자바 객체(members(list))
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(DATA_FILE_PATH), members);
        } catch (Exception e) {

            //데이터 저장을 실패하면
            log.error("회원 데이터 저장 실패", e);
            throw new RuntimeException("데이터 저장 실패", e);
        }
    }

    /**
     * 회원을 저장하는 메서드입니다.
     *
     * 사용 시나리오:
     * - 새로운 회원 가입 (id가 없음)
     * - 기존 회원 정보 수정 (id가 이미 있음)
     *
     * [동작 설명]
     * 1. member.getId()가 null이면 → "처음 저장하는 새 회원"이라는 뜻!
     * → 회원에게 고유번호(ID)를 새로 만들어서 달아준다.
     * - sequence.incrementAndGet(): 1씩 증가하는 번호를 만든다. (예: 1, 2, 3, ...)
     * → 그 다음, store(메모리 저장소)에 "id: member"로 저장한다.
     * 2. 만약 id가 이미 있다면(수정 용도 등)
     * → 그냥 그 id로 store에 덮어쓴다.
     * (이미 있던 회원 정보가 있으면 교체, 없으면 새로 추가)
     * 3. 마지막으로, 전체 회원 데이터를 파일에도 저장한다(영구 저장).
     *
     * 숙지:
     * - store.put(id, member)는 자바의 Map에서 "id를 key로 member 객체를 저장"한다는 뜻!
     * - saveDataToFile()을 반드시 호출해야 데이터가 실제 파일에도 반영됨.
     */
    @Override
    public void save(Member member) {
        // (1) 신규 회원인지 체크: id가 비어있으면 새로 만들어줌
        if (member.getId() == null) {
            // sequence: 자동 증가 숫자. 새 id 생성!
            long newId = sequence.incrementAndGet();
            member.setId(newId);
            store.put(newId, member); // 새 회원 저장
        } else {
            // 이미 id가 있으면, 해당 id로 저장(덮어쓰기 됨)
            store.put(member.getId(), member);
        }
        // (2) 모든 회원 정보를 파일로 저장(영구화)
        saveDataToFile();
    }

     @Override
     public Member findById(Long id) {
     return store.get(id);
     }//member에서 id값만 찾아옴

     @Override
     public List<Member> findAll() {
     return new ArrayList<>(store.values());
     } //모든 회원들의 값을 찾아옴

     @Override
     public void remove(Long id) { //회원을 삭제할 때 사용
         store.remove(id);
         saveDataToFile();
     }

     @Override
     public Member findByUserId(String userId) { //userId 값이 있는지 확인하는 메소드
         //sotre.values().stream() => map에 담긴 모든 member객체들을 보내줌
         //.filter() => 조건에 맞는 데이터만 걸러냄
         //.findAny() => 값에 맞는 데이터가 발견되는 즉시 중단하고 그 값 반환
         //.orElse() => 반복문을 다 돌았는데 값이 없으면 null 값 반환
         return store.values().stream()
            .filter(member -> member.getUserId().equals(userId))
            .findAny()
            .orElse(null);
     }
}
