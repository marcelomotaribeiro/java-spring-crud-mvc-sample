package br.dev.marcelo.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
public class Contact {

    @Id
    @NotEmpty
    private String id;

    @NotEmpty
    private String name;


}
