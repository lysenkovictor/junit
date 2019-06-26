package ua.skillsup.practice.junit.service;

import ua.skillsup.practice.junit.model.*;
import ua.skillsup.practice.junit.repository.BookingRepository;

import java.math.BigDecimal;
import java.util.List;

public class BookingManagerImpl implements BookingManager {

// клиент
// ид фильма
// места которые хочет купить клиент

    private final PaymentSystem paymentSystem;
    private final BookingRepository bookingRepository;
    private final SessionRegistry sessionRegistry;
    private BookingOrder bookingOrder;

    public BookingManagerImpl(PaymentSystem paymentSystem, BookingRepository bookingRepository, SessionRegistry sessionRegistry) {
        this.paymentSystem = paymentSystem;
        this.bookingRepository = bookingRepository;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public OrderId book(ClientId clientId, SessionId sessionId, List<Place> placesToBook) {
        SessionInfo sessionInfo = sessionRegistry.getSessionInfo(sessionId);// фильм с местами
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
                        System.out.println(priceOrder);
                        break;
                    } else {
                        throw new RuntimeException("Эти места уже заняты");
                    }
                } else {
                    throw new RuntimeException("Выбранного ряда не существует");
                }
            }

            try {
                paymentSystem.debit(clientId, priceOrder);
            } catch (NullPointerException e) {
                //откат
                // и бросить исключение
            } catch (RuntimeException e) {
//               //бросить исключение но не откатывать
            }

        }


        return bookingOrder.getOrderId();
    }

    @Override
    public void undoBooking(OrderId id) {

    }

}
