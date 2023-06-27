package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class ItemServiceTest {

    ItemService itemService;
    ItemRepository itemRepository;
    UserService userService;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void beforeEach() {
        userService = Mockito.mock(UserService.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(
                itemRepository,
                userService,
                bookingRepository,
                commentRepository,
                itemRequestRepository
        );
    }

    @Test
    void findAllByTextTestWithEmptyList() {
        User user = new User(1L, "testName", "test@mail.com");
        String text = "";

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user));

        List<ItemDto> results = itemService.search(text, 0, 10);

        Assertions.assertEquals(0, results.size());
    }

    @Test
    void saveTest() {
        User user = new User(1L, "testName", "test@mail.com");
        Item item = new Item(1L, "testName", "testDescription", true, user, null);
        ItemDto itemDto = ItemDto.builder()
                .name("testName")
                .description("testDescription")
                .available(true)
                .build();

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        ItemDto foundItem = itemService.create(itemDto, user.getId());

        Assertions.assertNotNull(foundItem);
        Assertions.assertEquals(itemDto.getName(), foundItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), foundItem.getDescription());
        Assertions.assertEquals(itemDto.getAvailable(), foundItem.getAvailable());
        Assertions.assertEquals(itemDto.getRequestId(), foundItem.getRequestId());
    }

    @Test
    void saveTestWithWrongUserId() {
        ItemDto itemDto = ItemDto.builder()
                .name("testName")
                .description("testDescription")
                .available(true)
                .build();

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenThrow(DataNotFoundException.class);

        Exception exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemService.create(itemDto, 1L));

        Assertions.assertNull(exception.getMessage());
    }

    @Test
    void saveTestWithRequest() {
        User user1 = new User(1L, "testName1", "test1@mail.com");
        User user2 = new User(2L, "testName2", "test2@mail.com");
        ItemRequest itemRequest =
                new ItemRequest(1L, "testDescription", user2, LocalDateTime.now());
        Item item =
                new Item(1L, "testName", "testDescription", true, user1, itemRequest);
        ItemDto itemDto = ItemDto.builder()
                .name("testName")
                .description("testDescription")
                .available(true)
                .build();

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));


        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        ItemDto foundItem = itemService.create(itemDto, user1.getId());

        Assertions.assertNotNull(foundItem);
        Assertions.assertEquals(itemDto.getName(), foundItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), foundItem.getDescription());
        Assertions.assertEquals(itemDto.getAvailable(), foundItem.getAvailable());
        Assertions.assertEquals(itemDto.getRequestId(), foundItem.getRequestId());
    }

    @Test
    void saveTestWithWrongRequestId() {
        User user = new User(1L, "testName", "test@mail.com");
        ItemDto itemDto = ItemDto.builder()
                .name("testName")
                .description("testDescription")
                .available(true)
                .requestId(999L)
                .build();

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user));

        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenThrow(DataNotFoundException.class);

        Exception exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemService.create(itemDto, 1L));

        Assertions.assertNull(exception.getMessage());
    }

    @Test
    void saveComment() {
        CommentDto commentDto = CommentDto.builder()
                .text("text")
                .authorName(null)
                .created(null)
                .build();
        CommentDto commentInfoDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("testName")
                .created(LocalDateTime.now())
                .build();
        User user = new User(1L, "testName", "test@mail.com");
        Item item = new Item(1L, "testName", "testDescription", true, user,  null);
        Comment comment = new Comment(1L, "text", item, user, LocalDateTime.now());
        Booking booking = new Booking(null, null, null, null, null, Status.APPROVED);
        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user));

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(comment);

        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                        Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.any(Status.class),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        CommentDto foundComment = itemService.addComment(item.getId(), 1L, commentDto);

        Assertions.assertNotNull(foundComment);
        Assertions.assertEquals(commentInfoDto.getText(), foundComment.getText());
        Assertions.assertEquals(commentInfoDto.getAuthorName(), foundComment.getAuthorName());
    }
}

