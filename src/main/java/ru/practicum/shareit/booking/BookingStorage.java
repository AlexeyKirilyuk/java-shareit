package ru.practicum.shareit.booking;

public interface BookingStorage {
    public Booking createBooking(Booking user);

    public int updateBooking(Booking user);

    public Booking getBookingById(int id);

    public void deleteBookingById(int id);

}
