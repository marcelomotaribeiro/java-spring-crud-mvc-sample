package br.dev.marcelo.services;

import br.dev.marcelo.domains.Contacts;
import br.dev.marcelo.entities.Contact;
import br.dev.marcelo.entities.Email;
import br.dev.marcelo.exceptions.ContactNotFoundException;
import br.dev.marcelo.exceptions.EmailAlreadyInUseException;
import br.dev.marcelo.exceptions.InvalidEmailException;
import br.dev.marcelo.models.ContactUpdateDto;
import br.dev.marcelo.models.ContactViewDto;
import br.dev.marcelo.repositories.ContactsRepository;
import br.dev.marcelo.repositories.EmailsRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContactsService implements Contacts {

    private final ContactsRepository contactsRepository;

    private final EmailsRepository emailsRepository;

    public ContactsService(ContactsRepository contactsRepository, EmailsRepository emailsRepository) {
        this.contactsRepository = contactsRepository;
        this.emailsRepository = emailsRepository;
    }

    private final BiFunction<Contact, List<String>, ContactViewDto> viewWithMailsList = (contact, emails) -> {
      final ContactViewDto contactViewDto = new ContactViewDto();
      contactViewDto.setId(contact.getId());
      contactViewDto.setName(contact.getName());
      contactViewDto.setEmails(emails);
      return contactViewDto;
    };

    private final BiFunction<String, EmailsRepository, List<String>> emailsFromDb = (contactId, emailsRepository) ->
            emailsRepository.findByContactId(contactId).stream()
                    .map(Email::getAddress).collect(Collectors.toList());

    private final BiFunction<Contact, EmailsRepository, ContactViewDto> viewWithMailsDb = (contact, emailsRepository) ->
            viewWithMailsList.apply(contact, emailsFromDb.apply(contact.getId(), emailsRepository));

    private ContactViewDto viewFromContact(Contact contact) {
        return viewWithMailsDb.apply(contact, emailsRepository);
    }

    private final Predicate<ContactUpdateDto> emailValidator = contactUpdateDto ->
            contactUpdateDto.getEmails().stream().allMatch(address -> EmailValidator.getInstance().isValid(address));

    private boolean contactValidate(String id, ContactUpdateDto contactUpdateDto) {
        if (id == null) {
            return contactUpdateDto.getEmails().stream()
                    .allMatch(address -> emailsRepository.findByAddress(address).isEmpty());
        } else {
            for (String email : contactUpdateDto.getEmails()) {
                final List<String> ids = emailsRepository.findByAddress(email).stream()
                        .map(Email::getContactId).distinct().collect(Collectors.toList());
                if (!ids.isEmpty() && !ids.get(0).equals(id)) {
                    return false;
                }
            }
            return true;
        }
    }

    private ContactViewDto getContactViewDto(ContactUpdateDto contactUpdateDto) {
        final Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setName(contactUpdateDto.getName());
        contactsRepository.save(contact);
        contactUpdateDto.getEmails().forEach(address -> {
            final Email email = new Email();
            email.setId(UUID.randomUUID().toString());
            email.setContactId(contact.getId());
            email.setAddress(address);
            emailsRepository.save(email);
        });
        return viewWithMailsList.apply(contact, contactUpdateDto.getEmails());
    }

    private ContactViewDto getContactViewDto(String id, ContactUpdateDto contactUpdateDto, Contact contact) {
        contact.setName(contactUpdateDto.getName());
        final List<Email> emails = emailsRepository.findByContactId(id);
        contactUpdateDto.getEmails().stream().filter(address -> emails.stream().noneMatch(
                email -> email.getAddress().equals(address))).forEach(address -> {
            final Email email = new Email();
            email.setId(UUID.randomUUID().toString());
            email.setContactId(id);
            email.setAddress(address);
            emailsRepository.save(email);
        });
        emails.stream().filter(email -> !contactUpdateDto.getEmails().contains(email.getAddress()))
                .forEach(emailsRepository::delete);
        return viewWithMailsList.apply(contact, contactUpdateDto.getEmails());
    }

    @Override
    public ContactViewDto post(ContactUpdateDto contactUpdateDto) {
        if (emailValidator.test(contactUpdateDto)) {
            if (contactValidate(null, contactUpdateDto)) {
                return getContactViewDto(contactUpdateDto);
            } else {
                throw new EmailAlreadyInUseException();
            }
        } else {
            throw new InvalidEmailException();
        }
    }

    @Override
    public ContactViewDto put(String id, ContactUpdateDto contactUpdateDto) {
        if (emailValidator.test(contactUpdateDto)) {
            if (contactValidate(id, contactUpdateDto)) {
                return contactsRepository.findById(id).map(
                        contact -> getContactViewDto(id, contactUpdateDto, contact))
                            .orElseThrow(ContactNotFoundException::new);
            } else {
                throw new EmailAlreadyInUseException();
            }
        } else {
            throw new InvalidEmailException();
        }
    }

    @Override
    public List<ContactViewDto> get() {
        return contactsRepository.findAll().stream().map(this::viewFromContact).collect(Collectors.toList());
    }

    @Override
    public ContactViewDto get(String id) {
        return contactsRepository.findById(id).map(this::viewFromContact)
                .orElseThrow(ContactNotFoundException::new);
    }

    @Override
    public void delete(String id) {
        final Contact contact = contactsRepository.findById(id)
                .orElseThrow(ContactNotFoundException::new);
        contactsRepository.delete(contact);
        emailsRepository.deleteAll(emailsRepository.findByContactId(id));
    }

}
