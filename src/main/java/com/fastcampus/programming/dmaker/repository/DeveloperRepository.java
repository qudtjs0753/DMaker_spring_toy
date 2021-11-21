package com.fastcampus.programming.dmaker.repository;

import com.fastcampus.programming.dmaker.code.StatusCode;
import com.fastcampus.programming.dmaker.entity.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeveloperRepository
        extends JpaRepository<Developer, Long> {
    /**
     * Optional : NullPointerException 방지해줌
    null이 올 수 있는 값을 감싸는 Wrapper class.
    아니 근데 이거 interface만 만들었는데 어떻게 쓰는거임?
     spring jpa에서 제공하는 기능.
     Query method를 추가해서 스프링에 알릴 수 있음.
    findBy 또는 countBy라는 규칙에 맞게 메서드를 작성하면 스프링이 자동 작성.
    이외에도 많으니 잘 보기
    어케 구현했죠 이걸?
     */
    Optional<Developer> findByMemberId(String memberId);
    List<Developer> findDevelopersByStatusCodeEquals(StatusCode statusCode);
}
