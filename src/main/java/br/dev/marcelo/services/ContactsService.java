package br.dev.marcelo.services;

import br.dev.marcelo.domains.Contacts;
import br.dev.marcelo.entities.Contact;
import br.dev.marcelo.entities.Email;
import br.dev.marcelo.exceptions.ContactNotFoundException;
import br.dev.marcelo.exceptions.EmailAlreadyInUseException;
import br.dev.marcelo.exceptions.InvalidEmailException;
import br.dev.marcelo.models.ContactDto;
import br.dev.marcelo.repositories.ContactsRepository;
import br.dev.marcelo.repositories.EmailsRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContactsService implements Contacts {

    private final ContactsRepository contactsRepository;

    private final EmailsRepository emailsRepository;

    private final EmailValidator emailValidator;

    public ContactsService(ContactsRepository contactsRepository, EmailsRepository emailsRepository) {
        this.contactsRepository = contactsRepository;
        this.emailsRepository = emailsRepository;
        this.emailValidator = EmailValidator.getInstance();
    }

    @Override
    public ContactDto post(ContactDto contactDto) {
        emailValidate(contactDto);
        emailUsedCheck(contactDto);
        final var contact =  new Contact();
        contact.setName(contactDto.getName());
        contactsRepository.save(contact);
        for (var emailAddress : contactDto.getEmails()) {
            emailAdd(contact.getId(), emailAddress);
        }
        return getContactResponse(contactDto, contact);
    }

    private void emailAdd(String contact, String emailAddress) {
        final var email = new Email();
        email.setContactId(contact);
        email.setAddress(emailAddress);
        emailsRepository.save(email);
    }

    private void emailUsedCheck(ContactDto contactDto) {
        for (var email : contactDto.getEmails()) {
            emailUsedCheck(email);
        }
    }

    private void emailUsedCheck(String email) {
        if (!emailsRepository.findByAddress(email).isEmpty()) {
            throw new EmailAlreadyInUseException(email);
        }
    }

    private void emailValidate(ContactDto contactDto) {
        for (var email : contactDto.getEmails()) {
            if (!emailValidator.isValid(email)) {
                throw new InvalidEmailException(email);
            }
        }
    }

    @Override
    public ContactDto put(String id, ContactDto contactDto) {
        emailValidate(contactDto);
        final Optional<Contact> contactOptional = contactsRepository.findById(id);
        if (contactOptional.isPresent()) {
            final var contact = contactOptional.get();
            contact.setName(contactDto.getName());
            final List<Email> emails = emailsRepository.findByContactId(id);
            for (var emailAddress : contactDto.getEmails()) {
                if (emails.stream().noneMatch(e -> e.getAddress().equals(emailAddress))) {
                    emailUsedCheck(emailAddress);
                    emailAdd(id, emailAddress);
                }
            }
            for (var email : emails) {
                if (contactDto.getEmails().stream().noneMatch(e -> e.equals(email.getAddress()))) {
                    emailsRepository.delete(email);
                }
            }
            return getContactResponse(contactDto, contact);
        } else {
            throw new ContactNotFoundException();
        }
    }

    private ContactDto getContactResponse(ContactDto contactDto, Contact contact) {
        contactDto.setId(contact.getId());
        return contactDto;
    }

    @Override
    public List<ContactDto> get() {
        return contactsRepository.findAll().stream().map(getContactDto()).collect(Collectors.toList());
    }

    @Override
    public ContactDto get(String id) {
        return contactsRepository.findById(id).map(getContactDto()).orElseThrow(ContactNotFoundException::new);
    }

    private Function<Contact, ContactDto> getContactDto() {
        return contact -> {
            final var contactDto = new ContactDto();
            contactDto.setId(contact.getId());
            contactDto.setName(contact.getName());
            emailsRepository.findByContactId(contact.getId()).forEach(email -> {
                if (contactDto.getEmails() == null) {
                    contactDto.setEmails(new ArrayList<>());
                }
                contactDto.getEmails().add(email.getAddress());
            });
            return contactDto;
        };
    }

    @Override
    public void delete(String id) {
        final var contact = contactsRepository.findById(id)
                .orElseThrow(ContactNotFoundException::new);
        contactsRepository.delete(contact);
        emailsRepository.deleteAll(emailsRepository.findByContactId(id));
    }

}
