package com.adb.ws.ui.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import com.adb.ws.io.entity.AddressEntity;
import com.adb.ws.service.impl.UserServiceImpl;
import com.adb.ws.shared.dto.AddressDto;
import com.adb.ws.shared.dto.UserDto;
import com.adb.ws.ui.model.response.UserRest;

class UserControllerTest {

	@InjectMocks
	UserController userController;
	
	@Mock
	UserServiceImpl userService;
	
	UserDto userDto;
	
	final String USER_ID = "dkj34lkj0003kkljd";
	
	@BeforeEach
	void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
		userDto = new UserDto();
		userDto.setFirstName("Roshan");
		userDto.setLastName("Chaudhary");
		userDto.setEncryptedPassword("12345678");
		userDto.setEmail("test@test.com");
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setEmailVerificationToken(null);
		userDto.setUserId(USER_ID);
		userDto.setAddresses(getAddressesDto());
	}

	@Test
	void testGetUser() {

		when(userService.getUserByUserId(anyString())).thenReturn(userDto);
		
		UserRest userRest = userController.getUser(USER_ID);
		
		assertNotNull(userRest);
		assertEquals(USER_ID, userRest.getUserId());
		assertEquals(userDto.getFirstName(), userRest.getFirstName());
		assertEquals(userDto.getLastName(), userRest.getLastName());
		assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());
	}

	private List<AddressDto> getAddressesDto() {
		AddressDto AddressDto = new AddressDto();
		AddressDto.setType("shipping");
		AddressDto.setCity("Vancouver");
		AddressDto.setCountry("Canada");
		AddressDto.setPostalCode("ABC123");
		AddressDto.setStreetName("123 Street name");

		AddressDto billingAddressDto = new AddressDto();
		billingAddressDto.setType("billling");
		billingAddressDto.setCity("Vancouver");
		billingAddressDto.setCountry("Canada");
		billingAddressDto.setPostalCode("ABC123");
		billingAddressDto.setStreetName("123 Street name");

		List<AddressDto> addresses = new ArrayList<>();
		addresses.add(AddressDto);
		addresses.add(billingAddressDto);

		return addresses;

	}
	
	private List<AddressEntity> getAddressesEntity(){
		
		List<AddressDto> addresses = getAddressesDto();
		
	    Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
	    
	    return new ModelMapper().map(addresses, listType);
	}
}
