package br.dev.marcelo.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Entity
@Data
public class Email {

    @Id
    @NotEmpty
    private String id = UUID.randomUUID().toString();

    @NotEmpty
    private String contactId;

    @NotEmpty
    @javax.validation.constraints.Email
    private String address;

}
