package ua.skillsup.practice.junit.service.impl;

import ua.skillsup.practice.junit.model.*;
import ua.skillsup.practice.junit.repository.BookingRepository;
import ua.skillsup.practice.junit.service.BookingManager;
import ua.skillsup.practice.junit.service.PaymentSystem;
import ua.skillsup.practice.junit.service.SessionRegistry;
import ua.skillsup.practice.junit.service.exception.NotFoundSeatNumber;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BookingManagerImpl implements BookingManager {


    private final PaymentSystem paymentSystem;
    private final BookingRepository bookingRepository;
    private final SessionRegistry sessionRegistry;


    public BookingManagerImpl(PaymentSystem paymentSystem, BookingRepository bookingRepository, SessionRegistry sessionRegistry) {
        this.paymentSystem = paymentSystem;
        this.bookingRepository = bookingRepository;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public OrderId book(ClientId clientId, SessionId sessionId, List<Place> placesToBook) {
        SessionInfo sessionInfo = sessionRegistry.getSessionInfo(sessionId);
        BigDecimal priceOrder = BigDecimal.ZERO;

        for (Place place : placesToBook) {
            int seatNumber = place.getSeatNumber();
            int rowNumber = place.getRow();
            for (Row rowSession : sessionInfo.getRows()) {
                if (rowNumber == rowSession.getRowNumber()) {
                    List<Integer> bookedSeats = rowSession.getBookedSeats();
                    if (!bookedSeats.contains(seatNumber)) {
                        bookedSeats.add(seatNumber);
                        priceOrder = priceOrder.add(rowSession.getPricePerPlace());
                        break;
                    } else {
                        throw new NotFoundSeatNumber(seatNumber, rowNumber);
                    }
                }
            }
        }

        if (priceOrder.compareTo(BigDecimal.ZERO) < 1) {
            throw new IllegalArgumentException("priceOrder shouldb greater than 0");
        }

        paymentSystem.debit(clientId, priceOrder);
        BookingOrder bookingOrder = new BookingOrder();
        bookingOrder.setOrderId(OrderId.of(System.nanoTime()));
        bookingOrder.setClientId(clientId);
        bookingOrder.setBookingTime(LocalDateTime.now());
        bookingOrder.setBookedPlaces(placesToBook);

        bookingRepository.save(bookingOrder);


        return bookingOrder.getOrderId();
    }

    @Override
    public void undoBooking(OrderId id) {

    }

}
