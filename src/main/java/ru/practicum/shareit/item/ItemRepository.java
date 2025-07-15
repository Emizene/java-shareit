package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByNameContainingIgnoreCaseAndAvailableIsTrueOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String name, String description);

    Optional<Item> findByIdAndOwnerId(Long itemId, Long userId);

    boolean existsByOwnerId(Long userId);

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.bookings WHERE i.owner.id = :ownerId")
    List<Item> findAllByOwnerId(@Param("ownerId") Long ownerId);
}
