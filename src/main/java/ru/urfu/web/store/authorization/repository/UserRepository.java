package ru.urfu.web.store.authorization.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.web.store.authorization.entity.User;

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
}
