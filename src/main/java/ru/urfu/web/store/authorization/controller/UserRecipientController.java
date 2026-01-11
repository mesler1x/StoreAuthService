package ru.urfu.web.store.authorization.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.urfu.web.store.authorization.entity.dto.Paging;
import ru.urfu.web.store.authorization.entity.dto.RecipientDto;
import ru.urfu.web.store.authorization.entity.dto.UserDto;
import ru.urfu.web.store.authorization.service.UserService;

import java.util.List;

@CrossOrigin(originPatterns = {"http://localhost:3000", "*"})
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRecipientController {
    private final UserService userService;

    @Operation(summary = "Получить информацию по пользователю")
    @GetMapping("/info/{userMail}")
    public UserDto getInfoByMail(@PathVariable String userMail) {
        return userService.getInfoByEmail(userMail);
    }

    @Operation(summary = "Удалить пользователя и все данные получателя по нему")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userMail}")
    public void deleteUser(@PathVariable String userMail) {
        userService.deleteUser(userMail);
    }

    @Operation(summary = "Обновить данные получателя у пользователя")
    @PutMapping("/recipient/update")
    public void updateRecipientDataToUser(@RequestBody @Valid RecipientDto recipientDto) {
        userService.updateRecipientDataToUser(recipientDto);
    }

    @Operation(summary = "Получить всех пользователей")
    @GetMapping
    public Paging<UserDto> findAll(@RequestParam(name = "limit", required = false, defaultValue = "100")
                                             Integer limit,
                                         @RequestParam(name = "offset", required = false, defaultValue = "0")
                                             Integer offset) {
        return userService.findAll(limit, offset);
    }
}
