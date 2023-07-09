package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.dto.User;

import java.util.HashMap;
import java.util.List;

public interface UserStorage extends JpaRepository<User, Long> {
}
