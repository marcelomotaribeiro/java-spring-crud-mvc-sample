package br.dev.marcelo;

import br.dev.marcelo.controllers.ContactsController;
import br.dev.marcelo.exceptions.ContactNotFoundException;
import br.dev.marcelo.exceptions.EmailAlreadyInUseException;
import br.dev.marcelo.exceptions.InvalidEmailException;
import br.dev.marcelo.models.ContactDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JavaSpringCrudMvcSampleApplicationTests {

	@Autowired
	ContactsController contactsController;

	@Test
	void applicationClassTest() {
		final var params = new String[]{};
		JavaSpringCrudMvcSampleApplication.main(params);
		assertEquals(0, params.length);
	}

	@Test
	void createContact() {
		final var contact = new ContactDto();
		contact.setName(UUID.randomUUID().toString());
		final List<String> emails = new ArrayList<>();
		emails.add(UUID.randomUUID().toString().concat("@test.com"));
		contact.setEmails(emails);
		assertEquals(HttpStatus.CREATED, contactsController.post(contact).getStatusCode(),
				"Create Marcelo contact");
	}

	@Test
	void createContactWithInvalidMail() {
		final var contact = new ContactDto();
		contact.setName(UUID.randomUUID().toString());
		final var emails = new ArrayList<String>();
		emails.add(UUID.randomUUID().toString().concat("@test.com"));
		emails.add("invalid");
		contact.setEmails(emails);
		try {
			contactsController.post(contact);
		} catch(InvalidEmailException ex) {
			assertEquals("Email [invalid] não possui um formato válido!", ex.getMessage());
		}
	}

	@Test
	void createContactWithMailAlreadyInUse() {
		final Consumer<List<String>> addEmail = emails -> emails.add(UUID.randomUUID().toString().concat(
				"@test.com"));
		final ContactDto contactA = new ContactDto();
		contactA.setName(UUID.randomUUID().toString());
		final var emailsA = new ArrayList<String>();
		addEmail.accept(emailsA);
		contactA.setEmails(emailsA);
		contactsController.post(contactA);
		final var contactB = new ContactDto();
		contactB.setName(UUID.randomUUID().toString());
		contactB.setEmails(emailsA);
		try {
			contactsController.post(contactB);
		} catch (EmailAlreadyInUseException ex) {
			assertEquals(String.format("Email %s já está em uso por outro registro", emailsA.get(0)), ex.getMessage());
		}
		final var emailsB = new ArrayList<String>();
		addEmail.accept(emailsB);
		contactB.setEmails(emailsB);
		assertEquals(HttpStatus.CREATED, contactsController.post(contactB).getStatusCode(),
				"Create contact Marcos");
	}

	@Test
	void updateContact() {
		final var contactUpdate = new ContactDto();
		contactUpdate.setName(UUID.randomUUID().toString());
		final var emails = new ArrayList<String>();
		emails.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdate.setEmails(emails);
		final var contactResponse = contactsController.post(contactUpdate);
		final var contactView = contactResponse.getBody();
		contactUpdate.getEmails().clear();
		contactUpdate.getEmails().add("marcelomotaribeiro@yahoo.com.br");
		assert contactView != null;
		assertEquals(HttpStatus.OK, contactsController.put(contactView.getId(), contactUpdate).getStatusCode(),
				"Update Marcelo contact");
	}

	@Test
	void updateContactWithInvalidMail() {
		final var contactUpdate = new ContactDto();
		contactUpdate.setName(UUID.randomUUID().toString());
		final var emails = new ArrayList<String>();
		emails.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdate.setEmails(emails);
		final var contactResponse = contactsController.post(contactUpdate);
		final var contactView = contactResponse.getBody();
		contactUpdate.getEmails().add("invalid");
		assert contactView != null;
		try {
			contactsController.put(contactView.getId(), contactUpdate);
		} catch (InvalidEmailException ex) {
			assertEquals("Email [invalid] não possui um formato válido!", ex.getMessage());
		}
	}

	@Test
	void updateContactWithMailAlreadyInUse() {
		final var contactUpdateA = new ContactDto();
		contactUpdateA.setName(UUID.randomUUID().toString());
		final var emailsA = new ArrayList<String>();
		emailsA.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdateA.setEmails(emailsA);
		contactsController.post(contactUpdateA);
		final var contactUpdateB = new ContactDto();
		contactUpdateB.setName(UUID.randomUUID().toString());
		final var emailsB = new ArrayList<String>();
		emailsB.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdateB.setEmails(emailsB);
		final var contactResponseB = contactsController.post(contactUpdateB);
		final var contactViewB = contactResponseB.getBody();
		contactUpdateB.getEmails().add(emailsA.get(0));
		assert contactViewB != null;
		try {
			contactsController.put(contactViewB.getId(), contactUpdateB);
		} catch (EmailAlreadyInUseException ex) {
			assertEquals(String.format("Email %s já está em uso por outro registro", emailsA.get(0)), ex.getMessage());
		}
		contactUpdateB.getEmails().remove(emailsA.get(0));
		assertEquals(HttpStatus.OK, contactsController.put(contactViewB.getId(), contactUpdateB).getStatusCode(),
				"Update contact Cris");
	}

	@Test
	void updateContactWithInvalidId() {
		final var contactUpdate = new ContactDto();
		contactUpdate.setName(UUID.randomUUID().toString());
		final var emails = new ArrayList<String>();
		emails.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdate.setEmails(emails);
		contactsController.post(contactUpdate);
		try {
			contactsController.put(UUID.randomUUID().toString(), contactUpdate);
		} catch (ContactNotFoundException ex) {
			assertEquals("Contato não encontrado!", ex.getMessage());
		}
		emails.clear();
		emails.add(UUID.randomUUID().toString().concat("@test.com"));
		try {
			contactsController.put(UUID.randomUUID().toString(), contactUpdate);
		} catch (ContactNotFoundException ex) {
			assertEquals("Contato não encontrado!", ex.getMessage());
		}
	}

	@Test
	void getAllContacts() {
		final var contactUpdateA = new ContactDto();
		contactUpdateA.setName(UUID.randomUUID().toString());
		final var emailsA = new ArrayList<String>();
		emailsA.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdateA.setEmails(emailsA);
		final var contactResponseA = contactsController.post(contactUpdateA);
		final var contactViewA = contactResponseA.getBody();
		final var contactUpdateB = new ContactDto();
		contactUpdateB.setName(UUID.randomUUID().toString());
		contactUpdateB.setEmails(emailsA);
		final var contactViewB = contactResponseA.getBody();
		final var allContacts = contactsController.get();
		assertEquals(HttpStatus.OK, allContacts.getStatusCode(), "All contacts");
		final var responseIds = Objects.requireNonNull(allContacts.getBody()).stream().map(ContactDto::getId)
							.collect(Collectors.toList());
		assert contactViewA != null;
		assertTrue(responseIds.contains(contactViewA.getId()),
				"All contacts contains Marcelo id");
		assert contactViewB != null;
		assertTrue(responseIds.contains(contactViewB.getId()),
				"All contacts contains Stephanie id");
	}

	@Test
	void getContactById() {
		final var contactUpdateA = new ContactDto();
		contactUpdateA.setName(UUID.randomUUID().toString());
		final var emailsA = new ArrayList<String>();
		emailsA.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdateA.setEmails(emailsA);
		final var contactResponseA = contactsController.post(contactUpdateA);
		final var contactViewA = contactResponseA.getBody();
		assert contactViewA != null;
		final var contactResponseB =
				contactsController.get(contactViewA.getId());
		final var contactViewB = contactResponseB.getBody();
		assert contactViewB != null;
		assertEquals(contactViewB.getId(), contactViewA.getId(), "Get contact by Id");
	}

	@Test
	void getContactByIdWithInvalidId() {
		try {
			contactsController.get(UUID.randomUUID().toString());
		} catch (ContactNotFoundException ex) {
			assertEquals("Contato não encontrado!", ex.getMessage());
		}
	}

	@Test
	void deleteContact() {
		final var contactUpdate = new ContactDto();
		contactUpdate.setName(UUID.randomUUID().toString());
		final var emails = new ArrayList<String>();
		emails.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdate.setEmails(emails);
		final var contactResponseA = contactsController.post(contactUpdate);
		final var contactViewA = contactResponseA.getBody();
		assert contactViewA != null;
		contactsController.delete(contactViewA.getId());
		final var allContacts = contactsController.get();
		final var responseIds = Objects.requireNonNull(allContacts.getBody()).stream().map(ContactDto::getId)
							.collect(Collectors.toList());
		assertFalse(responseIds.contains(contactViewA.getId()), "Delete contact");
	}

	@Test
	void deleteContactWithInvalidId() {
		try {
			contactsController.delete(UUID.randomUUID().toString());
		} catch (ContactNotFoundException ex) {
			assertEquals("Contato não encontrado!", ex.getMessage());
		}
	}

}
