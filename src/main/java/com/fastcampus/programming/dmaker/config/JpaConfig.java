package com.fastcampus.programming.dmaker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @Author: kbs
 */
//JPA metamodel must not be empty! 에러 발생 해결
//참고: https://xlffm3.github.io/spring%20&%20spring%20boot/JPAError/
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
