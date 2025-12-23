package be.vives.taskmanager.infrastructure.security;

import be.vives.taskmanager.domain.model.User;
import be.vives.taskmanager.domain.model.enumerator.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private User testUser;
    private String token;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);

        token = jwtService.generateToken(testUser);
    }

    @Test
    void shouldGenerateToken() {
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldValidateToken() {
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void shouldExtractUsername() {
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void shouldExtractRole() {
        String role = jwtService.extractRole(token);
        assertEquals("USER", role);
    }

    @Test
    void shouldNotValidateInvalidToken() {
        assertFalse(jwtService.validateToken("invalid.token.here"));
    }
}