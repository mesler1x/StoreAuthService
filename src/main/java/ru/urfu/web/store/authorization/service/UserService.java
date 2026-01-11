package ru.urfu.web.store.authorization.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.web.store.authorization.controller.exception.NotFoundException;
import ru.urfu.web.store.authorization.entity.dto.Paging;
import ru.urfu.web.store.authorization.entity.dto.RecipientDto;
import ru.urfu.web.store.authorization.entity.dto.UserDto;
import ru.urfu.web.store.authorization.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto getInfoByEmail(String userMail) {
        return userRepository.getInfoByMail(userMail).orElseThrow(
                () -> new NotFoundException("Пользователь с данным email не найден")
        );
    }

    @Transactional
    public void deleteUser(String userMail) {
        userRepository.delete(userMail);
    }

    @Transactional
    public void updateRecipientDataToUser(RecipientDto recipientDto) {
        userRepository.updateRecipientData(recipientDto);
    }

    public Paging<UserDto> findAll(Integer limit, Integer offset) {
        return userRepository.findAll(limit, offset);
    }
}
