package ru.practicum.shareit.comments.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthorName().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment fromCommentDto(CommentDto commentDto, Item item, User user) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .authorName(user)
                .item(item)
                .created(commentDto.getCreated())
                .build();
    }

    public static List<CommentDto> fromListComment(List<Comment> input) {
        return input.stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }
}
