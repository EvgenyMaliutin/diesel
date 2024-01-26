package ru.maliutin.diesel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.maliutin.diesel.security.PasswordResetToken;

import java.util.Optional;

/**
 * Репозиторий для работы с токенами восстановления пароля.
 */
@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    @Modifying
    @Query(value = """
            delete from password_reset_tokens where id_user = :user_id""",
            nativeQuery = true)
    void deleteAllByUserId(@Param("user_id")Long userId);

}
