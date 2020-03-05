package com.jackroot.app.ws.io.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jackroot.app.ws.io.entity.AddressEntity;
import com.jackroot.app.ws.io.entity.UserEntity;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {
	
	@Autowired
	UserRepository userRepository;
	
	private static boolean createRecords = false;
	private String userId = "1a2b3c";

	@BeforeEach
	void setUp() throws Exception {
		if (!createRecords) {
			createRecordes();
		}
	}

	@Test
	void testGetVeirifiedUsers() {
		Pageable pageableRequest = PageRequest.of(1, 1);
		Page<UserEntity> page = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
		assertNotNull(page);
		
		List<UserEntity> userEntities = page.getContent();
		assertNotNull(userEntities);
		assertTrue(userEntities.size() == 1);
	}
	
	@Test
	void testFindUserByFirstName() {
		String firstName = "Ivan";
		List<UserEntity> users = userRepository.findUserByFirstName(firstName);
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		UserEntity user = users.get(0);
		assertTrue(user.getFirstName().equals(firstName));
	}
	
	@Test
	void testFindUserByLastName() {
		String lastName = "Ivanov";
		List<UserEntity> users = userRepository.findUserByLastName(lastName);
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		UserEntity user = users.get(0);
		assertTrue(user.getLastName().equals(lastName));
	}
	
	@Test
	void testFindUserByKeyword() {
		String keyword = "ov";
		List<UserEntity> users = userRepository.findUserByKeyword(keyword);
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		UserEntity user = users.get(0);
		assertTrue(user.getLastName().contains(keyword) || 
				   user.getFirstName().contains(keyword));
	}
	
	@Test
	void testFindUserFirstNameAndLastNameByKeyword() {
		String keyword = "Iv";
		List<Object[]> users = userRepository.findUserFirstNameAndLastNameByKeyword(keyword);
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		Object[] user = users.get(0);
		String userFirstName = String.valueOf(user[0]);
		String userLastName = String.valueOf(user[1]);
		
		assertTrue(user.length == 2);
		assertNotNull(userFirstName);
		assertNotNull(userLastName);
		
		System.out.println("First name = " + userFirstName);
		System.out.println("Last name = " + userLastName);
	}
	
	@Test
	void testUpdateUserEmailVerificationStatus() {
		boolean newEmailVerificationStatus = false;
		userRepository.updateUserEmailVerificationStatus(newEmailVerificationStatus, userId);
		
		UserEntity storedUserDetails = userRepository.findByUserId(userId);
		boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();
		
		assertTrue(storedEmailVerificationStatus == newEmailVerificationStatus);
	}
	
	@Test
	void testFindUserEntityByUserId() {
		UserEntity userEntity = userRepository.findUserEntityByUserId(userId);
		
		assertNotNull(userEntity);
		assertTrue(userEntity.getUserId().equals(userId));
	}
	
	@Test
	void testGetUserEntityFullNameById() {
		List<Object[]> records = userRepository.getUserEntityFullNameById(userId);
		
		assertNotNull(records);
		assertTrue(records.size() == 1);
		
		Object[] userDetails = records.get(0);
		String firstName = String.valueOf(userDetails[0]);
		String lastName = String.valueOf(userDetails[1]);
		
		assertNotNull(firstName);
		assertNotNull(lastName);
	}
	
	@Test
	void testUpdateUserEntityEmailVerificationStatus() {
		boolean newEmailVerificationStatus = false;
		userRepository.updateUserEntityEmailVerificationStatus(newEmailVerificationStatus, userId);
		
		UserEntity storedUserDetails = userRepository.findByUserId(userId);
		boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();
		
		assertTrue(storedEmailVerificationStatus == newEmailVerificationStatus);
	}
	
	private void createRecordes() {
		// Prepare User Entity
		UserEntity userEntity = new UserEntity();
		userEntity.setFirstName("Ivan");
		userEntity.setLastName("Ivanov");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword("yyyy");
		userEntity.setEmail("test@test.com");
		userEntity.setEmailVerificationStatus(true);

		// Prepare User Addresses
		AddressEntity addressEntity = new AddressEntity();
		addressEntity.setType("shipping");
		addressEntity.setAddressId("ahgyt74hfy");
		addressEntity.setCity("Vancouver");
		addressEntity.setCountry("Canada");
		addressEntity.setPostalCode("ABCCDA");
		addressEntity.setStreetName("123 Street Address");

		List<AddressEntity> addresses = new ArrayList<>();
		addresses.add(addressEntity);

		userEntity.setAddresses(addresses);

		// Prepare Second User Entity
		UserEntity userEntity2 = new UserEntity();
		userEntity2.setFirstName("Ivan");
		userEntity2.setLastName("Ivanov");
		userEntity2.setUserId("1a2b3cddddd");
		userEntity2.setEncryptedPassword("xxx");
		userEntity2.setEmail("test1@test.com");
		userEntity2.setEmailVerificationStatus(true);

		// Prepare User Addresses for second User
		AddressEntity addressEntity2 = new AddressEntity();
		addressEntity2.setType("shipping");
		addressEntity2.setAddressId("ahgyt74hfywwww");
		addressEntity2.setCity("Vancouver");
		addressEntity2.setCountry("Canada");
		addressEntity2.setPostalCode("ABCCDA");
		addressEntity2.setStreetName("123 Street Address");

		List<AddressEntity> addresses2 = new ArrayList<>();
		addresses2.add(addressEntity2);

		userEntity2.setAddresses(addresses2);

		userRepository.save(userEntity2);
		userRepository.save(userEntity);
		
		createRecords = true;
	}

}
