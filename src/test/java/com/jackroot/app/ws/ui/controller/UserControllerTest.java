package com.jackroot.app.ws.ui.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jackroot.app.ws.service.UserService;
import com.jackroot.app.ws.shared.dto.AddressDTO;
import com.jackroot.app.ws.shared.dto.UserDto;
import com.jackroot.app.ws.ui.model.response.UserRest;

class UserControllerTest {
	
	@InjectMocks
	UserController userController;
	
	@Mock
	UserService userService;
	
	UserDto userDto;

	final String userId = "asdflvbpm43";

	@BeforeEach
	void setUp() throws Exception {
		 MockitoAnnotations.initMocks(this);
		 
		 userDto = new UserDto();
		 userDto.setFirstName("Ivan");
		 userDto.setLastName("Ivanov");
		 userDto.setEmail("ivanov@test.com");
		 userDto.setEmailVerificationStatus(Boolean.FALSE);
		 userDto.setEmailVerificationToken(null);
		 userDto.setUserId(userId);
		 userDto.setAddresses(getAddressesDto());
		 userDto.setEncryptedPassword("qwer532asdf");
	}

	private List<AddressDTO> getAddressesDto() {
		AddressDTO addressDto = new AddressDTO();
		addressDto.setType("shipping");
		addressDto.setCity("Kharkiv");
		addressDto.setCountry("Ukraine");
		addressDto.setPostalCode("ASD123");
		addressDto.setStreetName("123 Some Street");
		
		AddressDTO billingAddressDto = new AddressDTO();
		billingAddressDto.setType("billing");
		billingAddressDto.setCity("Kharkiv");
		billingAddressDto.setCountry("Ukraine");
		billingAddressDto.setPostalCode("ASD123");
		billingAddressDto.setStreetName("123 Some Street");
		
		List<AddressDTO> addresses = new ArrayList<>();
		addresses.add(addressDto);
		addresses.add(billingAddressDto);
		
		return addresses;
	}

	@Test
	final void testGetUser() {
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);
		
		UserRest userRest = userController.getUser(userId);
		
		assertNotNull(userRest);
		assertEquals(userId, userRest.getUserId());
		assertEquals(userDto.getFirstName(), userRest.getFirstName());
		assertEquals(userDto.getLastName(), userRest.getLastName());
		assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());
	}

}
