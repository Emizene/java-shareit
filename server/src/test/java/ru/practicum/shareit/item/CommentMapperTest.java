package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.dto.ChangeCommentDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {

    @InjectMocks
    private CommentMapper commentMapper;

    private final User author = User.builder()
            .id(1L)
            .name("Author Name")
            .email("author@yandex.ru")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("Item Name")
            .description("Item Description")
            .available(true)
            .owner(author)
            .build();

    private final Instant created = Instant.now();

    @Test
    void testToCommentDto_shouldMapAllFieldsCorrectly() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .author(author)
                .item(item)
                .created(created)
                .build();

        CommentResponseDto dto = commentMapper.toCommentDto(comment);

        assertThat(dto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("text", "Test comment")
                .hasFieldOrPropertyWithValue("authorName", "Author Name")
                .hasFieldOrPropertyWithValue("created", created);
    }

    @Test
    void testToCommentDto_shouldHandleNullAuthor() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .author(null)
                .item(item)
                .created(created)
                .build();

        CommentResponseDto dto = commentMapper.toCommentDto(comment);

        assertThat(dto)
                .hasFieldOrPropertyWithValue("authorName", null);
    }

    @Test
    void testToEntity_shouldMapAllFieldsCorrectly() {
        ChangeCommentDto dto = new ChangeCommentDto("New comment");

        Comment comment = commentMapper.toEntity(dto, author, item);

        assertThat(comment)
                .hasFieldOrPropertyWithValue("text", "New comment")
                .hasFieldOrPropertyWithValue("author", author)
                .hasFieldOrPropertyWithValue("item", item)
                .hasFieldOrProperty("created").isNotNull();
    }

    @Test
    void testToEntity_shouldHandleNullDto() {
        Comment comment = commentMapper.toEntity(new ChangeCommentDto("Text"), author, item);

        assertThat(comment)
                .hasFieldOrPropertyWithValue("text", "Text")
                .hasFieldOrPropertyWithValue("author", author)
                .hasFieldOrPropertyWithValue("item", item);
    }

    @Test
    void testToEntity_shouldHandleNullAuthorAndItem() {
        ChangeCommentDto dto = new ChangeCommentDto("New comment");

        Comment comment = commentMapper.toEntity(dto, null, null);

        assertThat(comment)
                .hasFieldOrPropertyWithValue("text", "New comment")
                .hasFieldOrPropertyWithValue("author", null)
                .hasFieldOrPropertyWithValue("item", null);
    }
}
