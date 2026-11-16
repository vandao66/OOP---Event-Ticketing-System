package exception;

public class TicketAlreadyUsedException extends AppException {
    public TicketAlreadyUsedException(String message) {
        super(message);
    }
}