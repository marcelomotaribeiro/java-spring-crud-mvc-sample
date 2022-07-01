package br.dev.marcelo.domains;

import br.dev.marcelo.models.ContactDto;

import java.util.List;

public interface Contacts {

    ContactDto post(ContactDto contactDto);

    ContactDto put(String id, ContactDto contactDto);

    List<ContactDto> get();

    ContactDto get(String id);

    void delete(String id);

}
