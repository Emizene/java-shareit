package ru.practicum.shareit.item.comment.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

@Data
@AllArgsConstructor
@Valid
@NoArgsConstructor
@Builder
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
    @Column(updatable = false, nullable = false)
    private Instant created;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "author_id")
    private User author;
}
