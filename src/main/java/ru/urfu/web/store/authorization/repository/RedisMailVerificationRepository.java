package ru.urfu.web.store.authorization.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisMailVerificationRepository {

    private final RedisTemplate<String, Integer> mailVerificationCodesRedisTemplate;
    private final ValueOperations<String, Integer> mailVerificationCodesValueOperations;

    public void put(String email, Integer code) {
        log.info("Email {} начинает отправку в redis", email);
        mailVerificationCodesValueOperations.set(email, code);
        mailVerificationCodesRedisTemplate.expire(email, 11, TimeUnit.MINUTES);
        log.info("Email {} закончил отправку в redis успешно", email);
    }

    public Optional<Integer> get(String email) {
        return Optional.ofNullable(mailVerificationCodesValueOperations.get(email));
    }

    public void delete(String email) {
        mailVerificationCodesRedisTemplate.delete(email);
    }
}
