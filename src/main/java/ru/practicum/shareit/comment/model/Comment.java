package ru.practicum.shareit.comment.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Valid
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant created;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "author_id")
    private User author;
}
