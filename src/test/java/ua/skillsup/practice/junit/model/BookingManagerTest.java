package ua.skillsup.practice.junit.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ua.skillsup.practice.junit.repository.BookingRepository;
import ua.skillsup.practice.junit.service.BookingManagerImpl;
import ua.skillsup.practice.junit.service.PaymentSystem;
import ua.skillsup.practice.junit.service.SessionRegistry;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class BookingManagerTest {


    @Mock
    private PaymentSystem paymentSystem;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SessionRegistry sessionRegistry = Mockito.mock(SessionRegistry.class);


    @Test
    public void testCase1() {
        //Given
        SessionId sessionId = SessionId.of(44);
        ClientId clientId = ClientId.of(124);
        List<Place> placeList = new ArrayList<>(asList( new Place(10,2)));

//        when(any(PaymentSystem.class)).thenReturn(paymentSystem);
        when(sessionRegistry.getSessionInfo(sessionId)).thenReturn(getSessionInfo());

        //When


        //Then

        BookingManagerImpl bookingManager = new BookingManagerImpl(paymentSystem, bookingRepository, sessionRegistry);
        bookingManager.book(clientId,sessionId,placeList);

    }

    public SessionInfo getSessionInfo() {
        Row row = new Row(10, 4, new ArrayList<Integer>(asList(2, 1, 3)), BigDecimal.valueOf(150));
        List<Row> rows = new ArrayList<>(asList(row));
        return new SessionInfo(SessionId.of(1), rows, LocalDateTime.now());
    }




}
