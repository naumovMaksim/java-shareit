package ru.practicum.shareit.exceptions;

public class DataAlreadyExistException extends RuntimeException {
    private final String parameter;

    public DataAlreadyExistException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
