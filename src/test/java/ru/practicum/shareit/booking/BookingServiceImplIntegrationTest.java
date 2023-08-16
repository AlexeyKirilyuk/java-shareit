package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplIntegrationTest {
    private final EntityManager entityManager;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemServiceImpl itemService;

    @Test
    void testAddNew() {
        UserDto userDto1 = userService.createUser(UserDto.builder()
                .name("User 1 name")
                .email("user1@email.com")
                .build());
        UserDto userDto2 = userService.createUser(UserDto.builder()
                .name("User 2 name")
                .email("user2@email.com")
                .build());
        ItemDto itemDto = itemService.createItem(

                ItemDto.builder()
                        .name("Item 1 name")
                        .description("Item 1 description")
                        .available(true)
                        .build(),
                userDto1.getId()
        );

        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = LocalDateTime.now().plusHours(3);
        BookingDto bookingDto = BookingDto.builder()
                .itemId(itemDto.getId())
                .start(start)
                .end(end)
                .build();
        Long bookerId = userDto2.getId();
        bookingService.createBooking(bookerId, bookingDto);

        TypedQuery<Booking> query = entityManager.createQuery("SELECT b from Booking b ", Booking.class);
        List<Booking> actualBookings = query.getResultList();

        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0).getId(), notNullValue());
        assertThat(actualBookings.get(0).getStart(), equalTo(start));
        assertThat(actualBookings.get(0).getEnd(), equalTo(end));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(itemDto.getId()));
        assertThat(actualBookings.get(0).getItem().getName(), equalTo("Item 1 name"));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(bookerId));
        assertThat(actualBookings.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }
}
