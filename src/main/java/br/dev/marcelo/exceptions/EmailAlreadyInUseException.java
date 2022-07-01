package br.dev.marcelo.exceptions;

public class EmailAlreadyInUseException extends RuntimeException {

    private final String email;

    public EmailAlreadyInUseException(String email) {
        this.email = email;
    }

    @Override
    public String getMessage() {
        return String.format("Email %s já está em uso por outro registro", email);
    }
}