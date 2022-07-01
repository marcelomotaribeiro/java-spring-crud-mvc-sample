package br.dev.marcelo.exceptions;

public class InvalidEmailException extends RuntimeException {

    private final String email;

    public InvalidEmailException(String email) {
        this.email = email;
    }

    @Override
    public String getMessage() {
        return String.format("Email [%s] não possui um formato válido!", email);
    }
}