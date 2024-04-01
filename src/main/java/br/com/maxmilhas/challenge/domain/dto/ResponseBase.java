package br.com.maxmilhas.challenge.domain.dto;

public class ResponseBase<T> {
    private final boolean success;
    private final String message;
    private final T returnedObject;

    public ResponseBase(T object) {
        this.success = true;
        this.message = "";
        this.returnedObject = object;
    }

    public ResponseBase(String errorMessage) {
        this.success = false;
        this.message = errorMessage;
        this.returnedObject = null;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getReturnedObject() {
        return returnedObject;
    }
}
