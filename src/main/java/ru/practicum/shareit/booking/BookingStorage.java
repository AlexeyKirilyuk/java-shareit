package ru.practicum.shareit.booking;

import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;

public interface BookingStorage {
    public Booking createBooking(Booking user);
    public int updateBooking(Booking user);
    public Booking getBookingById(int id);
    public void deleteBookingById(int id);

}
