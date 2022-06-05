package br.dev.marcelo;

import br.dev.marcelo.controllers.ContactsController;
import br.dev.marcelo.models.ContactUpdateDto;
import br.dev.marcelo.models.ContactViewDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SpringBootTest
class JavaSpringCrudMvcSampleApplicationTests {

	@Autowired
	ContactsController contactsController;


	@Test
	void createContact() {
		final ContactUpdateDto contact = new ContactViewDto();
		contact.setName(UUID.randomUUID().toString());
		final List<String> emails = new ArrayList<>();
		emails.add(UUID.randomUUID().toString().concat("@test.com"));
		contact.setEmails(emails);
		Assert.isTrue(contactsController.post(contact).getStatusCode().equals(HttpStatus.CREATED),
				"Create Marcelo contact");
	}

	@Test
	void createContactWithInvalidMail() {
		final ContactUpdateDto contact = new ContactViewDto();
		contact.setName(UUID.randomUUID().toString());
		final List<String> emails = new ArrayList<>();
		emails.add(UUID.randomUUID().toString().concat("@test.com"));
		emails.add("invalid");
		contact.setEmails(emails);
		Assert.isTrue(contactsController.post(contact).getStatusCode().equals(HttpStatus.BAD_REQUEST),
			"Invalid mail on post");
	}

	@Test
	void createContactWithMailAlreadyInUse() {
		final Consumer<List<String>> addEmail = emails -> emails.add(UUID.randomUUID().toString().concat("@test.com"));
		final ContactUpdateDto contactA = new ContactViewDto();
		contactA.setName(UUID.randomUUID().toString());
		List<String> emailsA = new ArrayList<>();
		addEmail.accept(emailsA);
		contactA.setEmails(emailsA);
		contactsController.post(contactA);
		final ContactUpdateDto contactB = new ContactViewDto();
		contactB.setName(UUID.randomUUID().toString());
		contactB.setEmails(emailsA);
		Assert.isTrue(contactsController.post(contactB).getStatusCode().equals(HttpStatus.CONFLICT),
				"Email already in use on post");
		List<String> emailsB = new ArrayList<>();
		addEmail.accept(emailsB);
		contactB.setEmails(emailsB);
		Assert.isTrue(contactsController.post(contactB).getStatusCode().equals(HttpStatus.CREATED),
				"Create contact Marcos");
	}

	@Test
	void updateContact() {
		final ContactUpdateDto contactUpdate = new ContactViewDto();
		contactUpdate.setName(UUID.randomUUID().toString());
		List<String> emails = new ArrayList<>();
		emails.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdate.setEmails(emails);
		final ResponseEntity<ContactViewDto> contactResponse = contactsController.post(contactUpdate);
		final ContactViewDto contactView = contactResponse.getBody();
		contactUpdate.getEmails().add("marcelomotaribeiro@yahoo.com.br");
		assert contactView != null;
		Assert.isTrue(contactsController.put(contactView.getId(), contactUpdate).getStatusCode().equals(
				HttpStatus.OK), "Update Marcelo contact");
	}

	@Test
	void updateContactWithInvalidMail() {
		final ContactUpdateDto contactUpdate = new ContactViewDto();
		contactUpdate.setName(UUID.randomUUID().toString());
		List<String> emails = new ArrayList<>();
		emails.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdate.setEmails(emails);
		final ResponseEntity<ContactViewDto> contactResponse = contactsController.post(contactUpdate);
		final ContactViewDto contactView = contactResponse.getBody();
		contactUpdate.getEmails().add("invalid");
		assert contactView != null;
		Assert.isTrue(contactsController.put(contactView.getId(), contactUpdate).getStatusCode()
						.equals(HttpStatus.BAD_REQUEST), "Invalid mail on put");
	}

	@Test
	void updateContactWithMailAlreadyInUse() {
		final ContactUpdateDto contactUpdateA = new ContactViewDto();
		contactUpdateA.setName(UUID.randomUUID().toString());
		List<String> emailsA = new ArrayList<>();
		emailsA.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdateA.setEmails(emailsA);
		contactsController.post(contactUpdateA);
		final ContactUpdateDto contactUpdateB = new ContactViewDto();
		contactUpdateB.setName(UUID.randomUUID().toString());
		List<String> emailsB = new ArrayList<>();
		emailsB.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdateB.setEmails(emailsB);
		final ResponseEntity<ContactViewDto> contactResponseB = contactsController.post(contactUpdateB);
		final ContactViewDto contactViewB = contactResponseB.getBody();
		contactUpdateB.getEmails().add(emailsA.get(0));
		assert contactViewB != null;
		Assert.isTrue(contactsController.put(contactViewB.getId(), contactUpdateB)
						.getStatusCode().equals(HttpStatus.CONFLICT), "Email already in use on put");
		contactUpdateB.getEmails().remove(emailsA.get(0));
		Assert.isTrue(contactsController.put(contactViewB.getId(), contactUpdateB)
						.getStatusCode().equals(HttpStatus.OK), "Update contact Cris");
	}

	@Test
	void updateContactWithInvalidId() {
		final ContactUpdateDto contactUpdate = new ContactViewDto();
		contactUpdate.setName(UUID.randomUUID().toString());
		List<String> emails = new ArrayList<>();
		emails.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdate.setEmails(emails);
		contactsController.post(contactUpdate);
		Assert.isTrue(contactsController.put(UUID.randomUUID().toString(), contactUpdate)
				.getStatusCode().equals(HttpStatus.CONFLICT), "Update contact with invalid id - Conflict");
		emails.clear();;
		emails.add(UUID.randomUUID().toString().concat("@test.com"));
		Assert.isTrue(contactsController.put(UUID.randomUUID().toString(), contactUpdate)
				.getStatusCode().equals(HttpStatus.NOT_FOUND), "Update contact with invalid id - Not found");
	}

	@Test
	void getAllContacts() {
		final ContactUpdateDto contactUpdateA = new ContactViewDto();
		contactUpdateA.setName(UUID.randomUUID().toString());
		List<String> emailsA = new ArrayList<>();
		emailsA.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdateA.setEmails(emailsA);
		final ResponseEntity<ContactViewDto> contactResponseA = contactsController.post(contactUpdateA);
		final ContactViewDto contactViewA = contactResponseA.getBody();
		final ContactUpdateDto contactUpdateB = new ContactViewDto();
		contactUpdateB.setName(UUID.randomUUID().toString());
		contactUpdateB.setEmails(emailsA);
		final ContactViewDto contactViewB = contactResponseA.getBody();
		final ResponseEntity<List<ContactViewDto>> allContacts = contactsController.get();
		Assert.isTrue(allContacts.getStatusCode().equals(HttpStatus.OK), "All contacts");
		final List<String> responseIds = Objects.requireNonNull(allContacts.getBody()).stream().map(ContactViewDto::getId)
							.collect(Collectors.toList());
		assert contactViewA != null;
		Assert.isTrue(responseIds.contains(contactViewA.getId()),
				"All contacts contains Marcelo id");
		assert contactViewB != null;
		Assert.isTrue(responseIds.contains(contactViewB.getId()),
				"All contacts contains Stephanie id");
	}

	@Test
	void getContactById() {
		final ContactUpdateDto contactUpdateA = new ContactViewDto();
		contactUpdateA.setName(UUID.randomUUID().toString());
		List<String> emailsA = new ArrayList<>();
		emailsA.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdateA.setEmails(emailsA);
		final ResponseEntity<ContactViewDto> contactResponseA = contactsController.post(contactUpdateA);
		final ContactViewDto contactViewA = contactResponseA.getBody();
		assert contactViewA != null;
		final ResponseEntity<ContactViewDto> contactResponseB =
				contactsController.get(contactViewA.getId());
		final ContactViewDto contactViewB = contactResponseB.getBody();
		assert contactViewB != null;
		Assert.isTrue(contactViewA.getId().equals(contactViewB.getId()), "Get contact by Id");
	}

	@Test
	void getContactByIdWithInvalidId() {
		Assert.isTrue(contactsController.get(UUID.randomUUID().toString())
				.getStatusCode().equals(HttpStatus.NOT_FOUND), "Get contact by id with invalid id");
	}

	@Test
	void deleteContact() {
		final ContactUpdateDto contactUpdate = new ContactViewDto();
		contactUpdate.setName(UUID.randomUUID().toString());
		List<String> emails = new ArrayList<>();
		emails.add(UUID.randomUUID().toString().concat("@test.com"));
		contactUpdate.setEmails(emails);
		final ResponseEntity<ContactViewDto> contactResponseA = contactsController.post(contactUpdate);
		final ContactViewDto contactViewA = contactResponseA.getBody();
		assert contactViewA != null;
		contactsController.delete(contactViewA.getId());
		final ResponseEntity<List<ContactViewDto>> allContacts = contactsController.get();
		final List<String> responseIds = Objects.requireNonNull(allContacts.getBody()).stream().map(ContactViewDto::getId)
							.collect(Collectors.toList());
		Assert.isTrue(!responseIds.contains(contactViewA.getId()), "Delete contact");
	}

	@Test
	void deleteContactWithInvalidId() {
		Assert.isTrue(contactsController.delete(
				UUID.randomUUID().toString()).getStatusCode().equals(HttpStatus.NOT_FOUND),
				"Delete contact with invalid id");
	}

}
