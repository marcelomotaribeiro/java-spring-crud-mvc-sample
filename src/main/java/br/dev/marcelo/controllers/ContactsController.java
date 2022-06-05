package br.dev.marcelo.controllers;

import br.dev.marcelo.domains.Contacts;
import br.dev.marcelo.exceptions.ContactNotFoundException;
import br.dev.marcelo.exceptions.EmailAlreadyInUseException;
import br.dev.marcelo.exceptions.InvalidEmailException;
import br.dev.marcelo.models.ContactUpdateDto;
import br.dev.marcelo.models.ContactViewDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("contacts")
@Tag(name = "Contacts operations", description = "Operations to maintain the contact register")
public class ContactsController {

    private final Contacts contacts;

    public ContactsController(Contacts contacts) {
        this.contacts = contacts;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create contact")
    public ResponseEntity<ContactViewDto> post(@RequestBody @Valid ContactUpdateDto contactUpdateDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(contacts.post(contactUpdateDto));
        } catch (EmailAlreadyInUseException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (InvalidEmailException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update contact")
    public ResponseEntity<ContactViewDto> put(@Parameter(description = "Contact identifier") @PathVariable String id,
                                               @RequestBody @Valid ContactUpdateDto contactUpdateDto) {
        try {
            return ResponseEntity.ok(contacts.put(id, contactUpdateDto));
        } catch (EmailAlreadyInUseException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (InvalidEmailException ex) {
            return ResponseEntity.badRequest().build();
        } catch(ContactNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieve all contacts")
    public ResponseEntity<List<ContactViewDto>> get() {
        return ResponseEntity.ok(contacts.get());
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieve contact by identifier")
    public ResponseEntity<ContactViewDto> get(@Parameter(description = "Contact identifier") @PathVariable String id) {
        try {
            return ResponseEntity.ok(contacts.get(id));
        } catch(ContactNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Remove contact by identifier")
    public ResponseEntity<String> delete(@Parameter(description = "Contact identifier") @PathVariable String id) {
        try {
            contacts.delete(id);
            return ResponseEntity.ok().build();
        } catch(ContactNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

}
