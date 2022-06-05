package br.dev.marcelo.repositories;

import br.dev.marcelo.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactsRepository extends JpaRepository<Contact, String> { }
