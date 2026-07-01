package com.portable.microservices.ms_administration.iam.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.portable.microservices.ms_administration.iam.domain.model.Account;
import com.portable.microservices.ms_administration.iam.domain.model.Role;
import com.portable.microservices.ms_administration.iam.domain.model.User;
import com.portable.microservices.ms_administration.iam.domain.ports.out.AccountPersistencePortOut;
import com.portable.microservices.ms_administration.iam.domain.ports.out.PasswordEncryptationPortOut;
import com.portable.microservices.ms_administration.iam.infrastructure.security.jwt.JwtService;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseIsolatedTest {
    @Mock
    private AccountPersistencePortOut accountPersistence;
    @Mock
    private PasswordEncryptationPortOut passwordEncryptation;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private LoginUseCase useCase;

    @Test
    void executeDebeRetornarTokenCuandoCredencialesSonValidas() {
        Account account = account("admin", "hashed", "ADMIN");
        when(accountPersistence.findByUsername("admin")).thenReturn(Optional.of(account));
        when(passwordEncryptation.matches("secret", "hashed")).thenReturn(true);
        when(jwtService.createToken("admin", "ADMIN", 1L)).thenReturn("jwt-token");

        String token = useCase.execute("admin", "secret");

        assertEquals("jwt-token", token);
    }

    @Test
    void executeDebeRechazarPasswordIncorrecto() {
        Account account = account("admin", "hashed", "ADMIN");
        when(accountPersistence.findByUsername("admin")).thenReturn(Optional.of(account));
        when(passwordEncryptation.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute("admin", "wrong"));

        verify(jwtService, never()).createToken("admin", "ADMIN", 1L);
    }

    private Account account(String username, String password, String roleName) {
        Role role = new Role(1L, roleName, ZonedDateTime.now());
        User user = new User(1L, UUID.randomUUID(), "Admin", "User", "12345678", Set.of(role),
                ZonedDateTime.now(), null);
        return new Account(1L, UUID.randomUUID(), user, 1L, 1L, username, password, true, ZonedDateTime.now());
    }
}
