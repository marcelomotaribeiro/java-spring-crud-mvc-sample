package br.dev.marcelo.domains;

import br.dev.marcelo.models.ContactUpdateDto;
import br.dev.marcelo.models.ContactViewDto;

import java.util.List;

public interface Contacts {

    ContactViewDto post(ContactUpdateDto contactUpdateDto);

    ContactViewDto put(String id, ContactUpdateDto contactUpdateDto);

    List<ContactViewDto> get();

    ContactViewDto get(String id);

    void delete(String id);

}
