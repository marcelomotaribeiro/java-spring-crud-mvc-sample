package br.dev.marcelo.controllers;

import br.dev.marcelo.domains.Contacts;
import br.dev.marcelo.models.ContactDto;
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
    public ResponseEntity<ContactDto> post(@RequestBody @Valid ContactDto contactDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contacts.post(contactDto));
    }

    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update contact")
    public ResponseEntity<ContactDto> put(@Parameter(description = "Contact identifier") @PathVariable String id,
                                          @RequestBody @Valid ContactDto contactDto) {
        return ResponseEntity.ok(contacts.put(id, contactDto));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieve all contacts")
    public ResponseEntity<List<ContactDto>> get() {
        return ResponseEntity.ok(contacts.get());
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieve contact by identifier")
    public ResponseEntity<ContactDto> get(@Parameter(description = "Contact identifier") @PathVariable String id) {
        return ResponseEntity.ok(contacts.get(id));
    }

    @DeleteMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Remove contact by identifier")
    public ResponseEntity<String> delete(@Parameter(description = "Contact identifier") @PathVariable String id) {
        contacts.delete(id);
        return ResponseEntity.ok().build();
    }

}
