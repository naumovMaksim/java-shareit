package ru.practicum.shareit.exceptions;

public class NotUniqException extends RuntimeException {
    private final String parameter;

    public NotUniqException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
