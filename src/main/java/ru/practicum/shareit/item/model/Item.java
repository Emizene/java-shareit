package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Valid
@NoArgsConstructor
@Builder
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @NotNull
    private Boolean available;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Booking> bookings;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> comments;

//    @Transient
//    public Booking getNextBooking() {
//        if (this.bookings == null) return null;
//        return this.bookings.stream()
//                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
//                .min(Comparator.comparing(Booking::getStart))
//                .orElse(null);
//    }
//
//    @Transient
//    public Booking getLastBooking() {
//        if (this.bookings == null) return null;
//        return this.bookings.stream()
//                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
//                .max(Comparator.comparing(Booking::getEnd))
//                .orElse(null);
//    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }
}
