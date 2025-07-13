package ru.practicum.shareit.comment.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
    @NotEmpty
    private Item item;

    @NotNull
    @Column(updatable = false)
    private LocalDateTime created = LocalDateTime.now();

    @ManyToOne
    @NotNull
    @JoinColumn(name = "author_id")
    private User author;
}
