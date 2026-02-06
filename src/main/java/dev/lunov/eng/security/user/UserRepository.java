package dev.lunov.eng.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailIgnoreCase(@NonNull String email);

    @Query("select u from User u where upper(u.firstName) = upper(:firstName) and upper(u.lastName) = upper(:lastName)")
    Optional<User> findByFullName(@Param("firstName") @NonNull String firstName, @Param("lastName") @NonNull String lastName);

    Optional<User> findByEmail(@NonNull String email);
}
