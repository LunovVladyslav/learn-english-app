package dev.lunov.eng.security.user;

import dev.lunov.eng.security.user.dto.UserCreateDTO;
import dev.lunov.eng.security.user.dto.UserUpdateDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserService extends UserDetailsService {
    User save(UserCreateDTO createDTO);
    User findById(UUID id);
    User findByEmail(String email);
    User findByFullName(String fullName);
    UUID update(UUID id, UserUpdateDTO updateDTO);
    void delete(UUID id);
    void hardDelete(UUID id);
    boolean existsById(UUID id);
    boolean existsByEmail(String email);
}
