package ru.practicum.shareit.comments;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comments.model.Comment;

import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(Long itemId);

    List<Comment> findAllByItemIdIn(List<Long> itemsId);
}
