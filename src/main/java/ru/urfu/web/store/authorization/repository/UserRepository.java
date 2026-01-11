package ru.urfu.web.store.authorization.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.web.store.authorization.controller.exception.NotFoundException;
import ru.urfu.web.store.authorization.entity.Recipient;
import ru.urfu.web.store.authorization.entity.User;
import ru.urfu.web.store.authorization.entity.dto.Paging;
import ru.urfu.web.store.authorization.entity.dto.RecipientDto;
import ru.urfu.web.store.authorization.entity.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public boolean isUserEmailExists(String email) {
        return Boolean.TRUE.equals(jdbcTemplate.query(
                """ 
                        SELECT EXISTS(
                            SELECT u.id FROM users u 
                            WHERE email = :email
                        ) as exists
                        """,
                new MapSqlParameterSource(
                        "email", email
                ),
                rs -> {
                    if (rs.next()) {
                        return rs.getBoolean("exists");
                    }

                    return Boolean.FALSE;
                }
        ));
    }

    public boolean isUserEmailVerified(String email) {
        return Boolean.TRUE.equals(jdbcTemplate.query(
                """ 
                        SELECT EXISTS(
                            SELECT u.id FROM users u 
                            WHERE email = :email
                            AND u.is_verified = true
                        ) as exists
                        """,
                new MapSqlParameterSource(
                        "email", email
                ),
                rs -> {
                    if (rs.next()) {
                        return rs.getBoolean("exists");
                    }

                    return Boolean.FALSE;
                }
        ));
    }

    public User save(String email) {
        return jdbcTemplate.query(
                """
                        INSERT INTO users (email) VALUES(
                            :email
                        )
                        RETURNING id, email, is_verified
                        """,
                new MapSqlParameterSource("email", email),
                rs -> {
                    if (rs.next()) {
                        return new User(
                                rs.getObject("id", UUID.class),
                                rs.getString("email"),
                                rs.getBoolean("is_verified")
                        );
                    }

                    return null;
                }
        );
    }

    @Transactional
    public void updateUserSetVerified(String email) {
        jdbcTemplate.update(
                """
                        UPDATE users s SET is_verified = TRUE,
                        updated = now() 
                        WHERE email = :email
                        """,
                new MapSqlParameterSource("email", email)
        );
    }

    public Optional<UserDto> getInfoByMail(String userMail) {
        return jdbcTemplate.query(
                """
                        SELECT
                            s.id as user_id,
                            s.email,
                            r.id as recipient_id,
                            r.first_name,
                            r.middle_name,
                            r.last_name,
                            r.address,
                            r.phone,
                            r.description
                        FROM users s LEFT JOIN
                        recipient r on s.id = r.user_id
                        WHERE email = :email
                        """,
                new MapSqlParameterSource("email", userMail),
                rs -> {
                    if (rs.next()) {
                        return Optional.of(
                                new UserDto(
                                        rs.getObject("user_id", UUID.class),
                                        rs.getString("email"),
                                        new Recipient(
                                                rs.getObject("recipient_id", UUID.class),
                                                rs.getString("description"),
                                                rs.getString("first_name"),
                                                rs.getString("middle_name"),
                                                rs.getString("last_name"),
                                                rs.getString("address"),
                                                rs.getString("phone")
                                        )
                                )
                        );
                    }
                    return Optional.empty();
                }
        );
    }

    public void delete(String userMail) {
        var user = getInfoByMail(userMail).orElseThrow(
                () -> new NotFoundException("Пользователь с данным email не найден")
        );
        jdbcTemplate.update(
                """
                        DELETE FROM users WHERE id = :userId
                        """,
                new MapSqlParameterSource("userId", user.getUserId())
        );
        jdbcTemplate.update(
                """
                        DELETE FROM recipient WHERE id = :recipientId
                        """,
                new MapSqlParameterSource("recipientId", user.getRecipient().getId())
        );
    }

    public void updateRecipientData(RecipientDto recipientDto) {
        var user = getInfoByMail(recipientDto.getUserMail()).orElseThrow(
                () -> new NotFoundException("Пользователь с данным email не найден")
        );
        if (user.getRecipient() != null && user.getRecipient().getId() != null) {
            jdbcTemplate.update(
                    """
                            UPDATE recipient SET
                                first_name = :firstName,
                                middle_name = :middleName,
                                last_name = :lastName,
                                address = :address,
                                phone = :phone,
                                description = :description
                            WHERE id = :recipientId
                            """,
                    new MapSqlParameterSource().addValue("recipientId", user.getRecipient().getId())
                            .addValue("firstName", recipientDto.getFirstName())
                            .addValue("middleName", recipientDto.getMiddleName())
                            .addValue("lastName", recipientDto.getLastName())
                            .addValue("address", recipientDto.getAddress())
                            .addValue("phone", recipientDto.getPhone())
                            .addValue("description", recipientDto.getDescription())
            );
        } else {
            jdbcTemplate.update(
                    """
                            INSERT INTO recipient (user_id, first_name, middle_name, last_name, address, phone, description)
                                VALUES (
                                        :userId,
                                    :firstName,
                                    :middleName,
                                    :lastName,
                                    :address,
                                    :phone,
                                    :description
                                )
                            """,
                    new MapSqlParameterSource()
                            .addValue("userId", user.getUserId())
                            .addValue("firstName", recipientDto.getFirstName())
                            .addValue("middleName", recipientDto.getMiddleName())
                            .addValue("lastName", recipientDto.getLastName())
                            .addValue("address", recipientDto.getAddress())
                            .addValue("phone", recipientDto.getPhone())
                            .addValue("description", recipientDto.getDescription())
            );
        }
    }

    public Paging<UserDto> findAll(Integer limit, Integer offset) {
        var total = jdbcTemplate.queryForObject(
                """
                            SELECT count(*) from users
                        """
                , new MapSqlParameterSource(), Long.class);
        List<UserDto> users = jdbcTemplate.query(
                """
                        SELECT
                            s.id as user_id,
                            s.email,
                            r.id as recipient_id,
                            r.first_name,
                            r.middle_name,
                            r.last_name,
                            r.address,
                            r.phone,
                            r.description
                        FROM users s LEFT JOIN
                        recipient r on s.id = r.user_id
                        LIMIT :limit OFFSET :offset
                        """,
                new MapSqlParameterSource().addValue("limit", limit)
                        .addValue("offset", offset),
                rs -> {
                    var resultList = new ArrayList<UserDto>();
                    while (rs.next()) {
                        resultList.add(new UserDto(
                                rs.getObject("user_id", UUID.class),
                                rs.getString("email"),
                                new Recipient(
                                        rs.getObject("recipient_id", UUID.class),
                                        rs.getString("description"),
                                        rs.getString("first_name"),
                                        rs.getString("middle_name"),
                                        rs.getString("last_name"),
                                        rs.getString("address"),
                                        rs.getString("phone")
                                )
                        ));
                    }
                    return resultList;
                }
        );
        return new Paging<>(
                total,
                limit,
                offset,
                users
        );
    }
}
