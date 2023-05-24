package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.NotUniqException;
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
        throwNotUniqException(user);
        User createdUser = userRepository.create(user);
        return toUserDto(createdUser);
    }

    @Override
    public UserDto update(User user, Long id) {
        User updatedUser = userRepository.findById(id);
        if (user.getEmail() != null) {
            if (!updatedUser.getEmail().equals(user.getEmail())) {
                throwNotUniqException(user);
            }
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        return toUserDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        getById(id);
        userRepository.delete(id);
    }

    private void throwNotUniqException(User user) {
        for (User checkUser : userRepository.findAll()) {
            if (user.getEmail().equals(checkUser.getEmail())) {
                log.error("Пользователь с таким email уже зарегестрирован");
                throw new NotUniqException("Пользователь с таким email уже зарегестрирован");
            }
        }
    }
}
