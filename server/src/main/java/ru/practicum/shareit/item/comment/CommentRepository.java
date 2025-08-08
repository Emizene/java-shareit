package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.comment.model.Comment;

import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Set<Comment> getAllByItemId(Long itemId);
}
