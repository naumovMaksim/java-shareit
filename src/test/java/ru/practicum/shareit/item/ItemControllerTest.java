package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
class ItemControllerTest {
    private final ItemController controller;
    private final UserController userController;
    private ItemDto item;
    private User user;

    @BeforeEach
    void setUp() {

        item = ItemDto.builder()
                .name("Дрель")
                .description("Сверлит")
                .available(true)
                .comments(Collections.emptyList())
                .build();

        user = User.builder()
                .name("Макс")
                .email("email@email.ru")
                .build();
        userController.create(user);
        controller.create(1L, item);
    }

    @Test
    void findAll() {
        item.setId(1L);

        assertEquals(List.of(item), controller.findAll(1L));
    }

    @Test
    void findById() {
        item.setId(1L);

        assertEquals(item, controller.findById(item.getId(), user.getId()));
    }

    @Test
    void findByIdWrongId() {
        item.setId(1L);
        final DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> controller.findById(2L, user.getId()));

        assertEquals("Предмет с id 2 не найден", exception.getParameter());
    }

    @Test
    void create() {
        Item newItem = Item.builder()
                .id(2L)
                .name("Бензопила")
                .description("Пилит")
                .available(true)
                .build();

        assertEquals(toItemDto(newItem), controller.create(1L, toItemDto(newItem)));
    }

    @Test
    void update() {
        controller.create(1L, item);
        Item updatedItem = Item.builder()
                .id(1L)
                .name("Бензопила")
                .description("Пилит")
                .available(true)
                .build();
        ItemDto itemDto = toItemDto(updatedItem);
        itemDto.setComments(Collections.emptyList());
        controller.update(itemDto, 1L, user.getId());

        assertEquals(itemDto, controller.findById(1L, user.getId()));
    }

    @Test
    void updateWithWrongUser() {
        Item updatedItem = Item.builder()
                .id(1L)
                .name("Бензопила")
                .description("Пилит")
                .available(true)
                .build();
        User user2 = User.builder()
                .name("Макс")
                .email("d@g.ru")
                .build();
        userController.create(user2);
        final DataNotFoundException exception = assertThrows(DataNotFoundException.class, () ->
                controller.update(toItemDto(updatedItem), 1L, 2L));

        assertEquals("Этот предмет не принадлежит пользователю с id 2", exception.getParameter());
    }

    @Test
    void search() {
        Item itemToSearch = Item.builder()
                .id(2L)
                .name("Бензопила")
                .description("Пилит")
                .available(true)
                .build();
        User user2 = User.builder()
                .name("Макс")
                .email("d@g.ru")
                .build();
        userController.create(user2);
        controller.create(user2.getId(), toItemDto(itemToSearch));

        assertEquals(List.of(toItemDto(itemToSearch)), controller.search("Бензопила"));
    }

    @Test
    void searchWithWrongData() {
        Item itemToSearch = Item.builder()
                .id(2L)
                .name("Бензопила")
                .description("Пилит")
                .available(true)
                .build();
        User user2 = User.builder()
                .name("Макс")
                .email("d@g.ru")
                .build();
        userController.create(user2);
        controller.create(user2.getId(), toItemDto(itemToSearch));

        assertEquals(Collections.emptyList(), controller.search(""));
    }

    @Test
    void addComment() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName(user.getName())
                .created(LocalDateTime.now())
                .build();
        controller.addComment(item.getId(), user.getId(), commentDto);

        assertEquals(List.of(commentDto), controller.findById(item.getId(), user.getId()).getComments());
    }
}