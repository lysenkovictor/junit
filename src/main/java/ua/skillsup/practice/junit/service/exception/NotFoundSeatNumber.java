package ua.skillsup.practice.junit.service.exception;

public class NotFoundSeatNumber extends RuntimeException{

    public NotFoundSeatNumber(int seatNumber, int rowNumber) {
        super(String.format("seat is occupied: %s, row: %s", seatNumber, rowNumber));
    }
}
