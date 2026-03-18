package com.example.usermanagement.repository;

import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("password");
        user.setIsActive(true);

        entityManager.persist(user);
        entityManager.flush();

        Optional<User> found = userRepository.findByUsername("testuser");

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void findByUsername_NotFound_ReturnsEmpty() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void findByEmail_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("password");
        user.setIsActive(true);

        entityManager.persist(user);
        entityManager.flush();

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void existsByUsername_True() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("password");
        user.setIsActive(true);

        entityManager.persist(user);
        entityManager.flush();

        assertTrue(userRepository.existsByUsername("testuser"));
    }

    @Test
    void existsByUsername_False() {
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void existsByEmail_True() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("password");
        user.setIsActive(true);

        entityManager.persist(user);
        entityManager.flush();

        assertTrue(userRepository.existsByEmail("test@example.com"));
    }

    @Test
    void existsByEmail_False() {
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void findAllActive_Success() {
        User activeUser = new User();
        activeUser.setUsername("activeuser");
        activeUser.setEmail("active@example.com");
        activeUser.setPasswordHash("password");
        activeUser.setIsActive(true);

        User inactiveUser = new User();
        inactiveUser.setUsername("inactiveuser");
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setPasswordHash("password");
        inactiveUser.setIsActive(false);

        entityManager.persist(activeUser);
        entityManager.persist(inactiveUser);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> activeUsers = userRepository.findAllActive(pageable);

        assertEquals(1, activeUsers.getTotalElements());
        assertEquals("activeuser", activeUsers.getContent().get(0).getUsername());
    }

    @Test
    void findByUsernameWithRoles_Success() {
        Role role = new Role();
        role.setName("USER");
        role.setDescription("Standard user");
        entityManager.persist(role);

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("password");
        user.setIsActive(true);
        user.addRole(role);

        entityManager.persist(user);
        entityManager.flush();

        Optional<User> found = userRepository.findByUsernameWithRoles("testuser");

        assertTrue(found.isPresent());
        assertEquals(1, found.get().getRoles().size());
        assertTrue(found.get().getRoles().stream()
                .anyMatch(r -> r.getName().equals("USER")));
    }
}
