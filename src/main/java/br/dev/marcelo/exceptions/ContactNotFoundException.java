package br.dev.marcelo.exceptions;

public class ContactNotFoundException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Contato não encontrado!";
    }

}