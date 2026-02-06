package dev.lunov.eng.security.user.impl;

import dev.lunov.eng.security.user.Role;
import dev.lunov.eng.security.user.User;
import dev.lunov.eng.security.user.UserRepository;
import dev.lunov.eng.security.user.UserService;
import dev.lunov.eng.security.user.dto.UserCreateDTO;
import dev.lunov.eng.security.user.dto.UserUpdateDTO;
import dev.lunov.eng.security.user.exceptions.UserAlreadyExistsException;
import dev.lunov.eng.security.user.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User save(UserCreateDTO createDTO) {
        String email = createDTO.email().toLowerCase();
        if (existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        if (!createDTO.password().equals(createDTO.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        return userRepository.save(User.builder()
                .firstName(createDTO.firstName())
                .lastName(createDTO.lastName())
                .email(email)
                .password(passwordEncoder.encode(createDTO.password()))
                .enabled(true)
                .role(Role.ROLE_USER)
                .build());
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User with id: %s not found".formatted(id))
        );
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase()).orElseThrow(
                () -> new UserNotFoundException("User with email: %s not found".formatted(email))
        );
    }

    @Override
    public User findByFullName(String fullName) {
        var firstName = fullName.substring(0, 1).toUpperCase();
        var lastName = fullName.substring(1).toUpperCase();
        return userRepository.findByFullName(firstName, lastName).orElseThrow(
                () -> new UserNotFoundException("User with name: %s not found".formatted(fullName))
        );
    }

    @Override
    public UUID update(UUID id, UserUpdateDTO updateDTO) {
        var existingUser = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User with id: %s not found".formatted(id))
        );
        Optional.ofNullable(updateDTO.firstName()).ifPresent(existingUser::setFirstName);
        Optional.ofNullable(updateDTO.lastName()).ifPresent(existingUser::setLastName);
        Optional.ofNullable(updateDTO.email()).map(String::toLowerCase).ifPresent(existingUser::setEmail);
        Optional.ofNullable(updateDTO.password())
                .map(passwordEncoder::encode)
                .ifPresent(existingUser::setPassword);
        Optional.ofNullable(updateDTO.enabled()).ifPresent(existingUser::setEnabled);
        return userRepository.save(existingUser).getId();
    }

    @Override
    public void delete(UUID id) {
        var user = userRepository.findById(id).orElseThrow();
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void hardDelete(UUID id) {
        if (existsById(id)) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading User: {}", username);
        var user = this.userRepository.findByEmail(username).orElseThrow();
        user.updateLastLogin();
        return userRepository.save(user);
    }
}
