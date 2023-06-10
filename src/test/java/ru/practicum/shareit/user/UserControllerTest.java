package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {
    private final UserController controller;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Макс")
                .email("email@email.ru")
                .build();
        controller.create(user);
    }

    @Test
    void getAll() {
        assertEquals(List.of(toUserDto(user)), controller.getAll());
    }

    @Test
    void getById() {
        assertEquals(toUserDto(user), controller.getById(1L));
    }

    @Test
    void getByIdWrongId() {
        final DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> controller.getById(2L));

        assertEquals("Пользователь с id 2 не найден", exception.getParameter());
    }

    @Test
    void create() {
        User userForCreate = User.builder()
                .name("Max")
                .email("e@mail.ru")
                .build();
        controller.create(userForCreate);

        assertEquals(toUserDto(userForCreate), controller.getById(2L));
    }

    @Test
    void createWithNotUniqueEmail() {
        User userForUpdate = User.builder()
                .name("Max")
                .email("email@email.ru")
                .build();
        final DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () ->
                controller.create(userForUpdate));

        assertEquals(DataIntegrityViolationException.class, exception.getClass());
    }

    @Test
    void update() {
        User userForUpdate = User.builder()
                .id(1L)
                .name("Max")
                .email("e@mail.ru")
                .build();
        controller.update(userForUpdate, 1L);

        assertEquals(toUserDto(userForUpdate), controller.getById(1L));
    }

    @Test
    void delete() {
        assertEquals(List.of(toUserDto(user)), controller.getAll());
        controller.delete(1L);
        assertEquals(Collections.emptyList(), controller.getAll());
    }
}