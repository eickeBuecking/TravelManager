package de.eicke.exceptions;

public class TravelManagerException extends Exception {
    private static final long serialVersionUID = 1L;

    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }
    public TravelManagerException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
    public TravelManagerException() {
        super();
    }

}
