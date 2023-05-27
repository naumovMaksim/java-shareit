package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.DataAlreadyExistException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emailUniqSet = new HashSet<>();
    private Long id = 0L;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public User create(User user) {
        if (emailUniqSet.contains(user.getEmail())) {
            throw new DataAlreadyExistException("Email: " + user.getEmail() + " уже зарегестрирован");
        }
        user.setId(++id);
        users.put(user.getId(), user);
        emailUniqSet.add(user.getEmail());
        return users.get(user.getId());
    }

    @Override
    public User update(User user, Long id) {
        if (user.getEmail() != null) {
            emailUniqSet.remove(users.get(id).getEmail());
        }
        if (emailUniqSet.contains(user.getEmail())) {
            throw new DataAlreadyExistException("Email: " + user.getEmail() + " уже зарегестрирован");
        }
        User updatedUser = users.get(id);
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        users.put(id, updatedUser);
        emailUniqSet.add(user.getEmail());
        return users.get(id);
    }

    @Override
    public void delete(Long id) {
        emailUniqSet.remove(users.get(id).getEmail());
        users.remove(id);
    }
}
