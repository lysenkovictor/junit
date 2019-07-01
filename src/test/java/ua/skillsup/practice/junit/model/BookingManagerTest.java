package ua.skillsup.practice.junit.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import ua.skillsup.practice.junit.repository.BookingRepository;
import ua.skillsup.practice.junit.service.impl.BookingManagerImpl;
import ua.skillsup.practice.junit.service.PaymentSystem;
import ua.skillsup.practice.junit.service.SessionRegistry;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class BookingManagerTest {


    @Mock
    private PaymentSystem paymentSystem = Mockito.mock(PaymentSystem.class);

    @Mock
    private BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

    @Mock
    private SessionRegistry sessionRegistry = Mockito.mock(SessionRegistry.class);

    private SessionId sessionId = SessionId.of(44);
    private ClientId clientId = ClientId.of(124);
    private List<Place> placeList = getPlaceList();
    private SessionInfo sessionInfo = getSessionInfo();
    private BigDecimal priceExpected = BigDecimal.valueOf(900);

    private BookingManagerImpl bookingManager = new BookingManagerImpl(paymentSystem, bookingRepository, sessionRegistry);

    @Test
    @DisplayName("should be return OrderId")
    public void testCase1() {
        //Given
        when(sessionRegistry.getSessionInfo(sessionId)).thenReturn(sessionInfo);
        doNothing().when(paymentSystem).debit(clientId,BigDecimal.ZERO);
        BookingManagerImpl bookingManager = new BookingManagerImpl(paymentSystem, bookingRepository, sessionRegistry);

        //When
        OrderId orderId = bookingManager.book(clientId,sessionId,placeList);

        //Then
        assertThat(orderId).isNotNull();


    }

    @Test
    @DisplayName("should be debit")
    public void testCase2() {
        //Given
        when(sessionRegistry.getSessionInfo(sessionId)).thenReturn(sessionInfo);
        doNothing().when(paymentSystem).debit(clientId,BigDecimal.ZERO);
        BookingManagerImpl bookingManager = new BookingManagerImpl(paymentSystem, bookingRepository, sessionRegistry);

        //When
        bookingManager.book(clientId, sessionId, placeList);

        //Then
        verify(paymentSystem).debit(clientId,priceExpected);


    }

    @Test
    @DisplayName("should be formed BookingOrder")
    public void testCase3() {
        //Given
        ArgumentCaptor<BookingOrder> captor = ArgumentCaptor.forClass(BookingOrder.class);

        when(sessionRegistry.getSessionInfo(sessionId)).thenReturn(sessionInfo);
        doNothing().when(paymentSystem).debit(clientId,BigDecimal.ZERO);
        doNothing().when(bookingRepository).save(captor.capture());

        //When
        bookingManager.book(clientId, sessionId, placeList);

        //Then
        verify(bookingRepository).save(any(BookingOrder.class));
        assertThat(captor.getValue()).isEqualTo(getBookingOrderExpected(captor));

    }

    public SessionInfo getSessionInfo() {
        Row row = new Row(10, 4, new ArrayList(asList()), BigDecimal.valueOf(300));
        Row row2 = new Row(11, 5, new ArrayList(asList(2, 1, 3)), BigDecimal.valueOf(150));
        List<Row> rows = new ArrayList<>(asList(row,row2));
        return new SessionInfo(SessionId.of(1), rows, LocalDateTime.now());
    }

    public  List<Place> getPlaceList() {
        List<Place> placeList = new ArrayList();
        Place place = new Place(10,6);
        Place place2 = new Place(10,3);
        Place place3 = new Place(10,7);
        placeList.add(place);
        placeList.add(place2);
        placeList.add(place3);

        return placeList;
    }

    public BookingOrder getBookingOrderExpected(ArgumentCaptor<BookingOrder> captor) {

        LocalDateTime faceBookingTime = LocalDateTime.now();
        OrderId faceOrderId = OrderId.of(1111);
        captor.getValue().setBookingTime(faceBookingTime);
        captor.getValue().setOrderId(faceOrderId);

        BookingOrder bookingOrder = new BookingOrder();
        bookingOrder.setOrderId(faceOrderId);
        bookingOrder.setClientId(this.clientId);
        bookingOrder.setBookingTime(faceBookingTime);
        bookingOrder.setBookedPlaces(this.getPlaceList());

        return bookingOrder;
    }


}
