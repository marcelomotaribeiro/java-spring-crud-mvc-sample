package br.dev.marcelo.repositories;

import br.dev.marcelo.entities.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailsRepository extends JpaRepository<Email, String> {

    List<Email> findByContactId(String contactId);

    List<Email> findByAddress(String address);

}
