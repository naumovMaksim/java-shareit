package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.DataAlreadyExistException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(toUserDto(user));
        }
        return users;
    }

    @Override
    public UserDto getById(Long id) {
        if (userRepository.findById(id) == null) {
            log.error("Пользователь с id {} не найден", id);
            throw new DataNotFoundException(String.format("Пользователь с id %d не найден", id));
        }
        return toUserDto(userRepository.findById(id));
    }

    @Override
    public UserDto create(@Valid User user) {
        throwDataAlreadyExistException(user);
        User createdUser = userRepository.create(user);
        return toUserDto(createdUser);
    }

    @Override
    public UserDto update(User user, Long id) {
        return toUserDto(userRepository.update(user, id));
    }

    @Override
    public void delete(Long id) {
        getById(id);
        userRepository.delete(id);
    }

    private void throwDataAlreadyExistException(User user) {
        for (User checkUser : userRepository.findAll()) {
            if (user.getEmail().equals(checkUser.getEmail())) {
                log.error("Пользователь с таким email уже зарегестрирован");
                throw new DataAlreadyExistException("Пользователь с таким email уже зарегестрирован");
            }
        }
    }
}
