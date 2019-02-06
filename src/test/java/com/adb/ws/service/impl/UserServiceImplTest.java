package com.adb.ws.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.adb.ws.exceptions.UserServiceException;
import com.adb.ws.io.entity.AddressEntity;
import com.adb.ws.io.entity.UserEntity;
import com.adb.ws.repository.UserRepository;
import com.adb.ws.shared.dto.AddressDto;
import com.adb.ws.shared.dto.UserDto;
import com.adb.ws.shared.dto.utils.AmazonSES;
import com.adb.ws.shared.dto.utils.Utils;

class UserServiceImplTest {

	@InjectMocks // Mock annotation that allows user service impl component to allow mock
					// autowired component for eg. userRepository
	UserServiceImpl userServiceImpl;

	@Mock
	UserRepository userRepository;

	@Mock
	Utils utils;
	
	@Mock
	AmazonSES amazonSES;
	
	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	String userId = "abcdef";
	String userPassword = "4849dmd44dldk";
	
	UserEntity userEntity; //// Stub object, hard coded object created for testing
	UserDto userDto;
	
	@BeforeEach
	void setUp() throws Exception {
		// For Mockito to instantiate this object
		MockitoAnnotations.initMocks(this);
		
		// Stub object, hard coded object created for testing
		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Roshan");
		userEntity.setLastName("Chaudhary");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(userPassword);
		userEntity.setEmail("test@test.com");
		userEntity.setEmailVerificationToken("7htnfhr758");
		userEntity.setAddresses(getAddressesEntity());
		
		userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Roshan");
		userDto.setLastName("Chaudhary");
		userDto.setPassword("12345678");
		userDto.setEmail("test@test.com");
	}

	@Test
	void testGetUser() {

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

		/*
		 * In above code we created UserEntity stub obj to test in assert functions
		 * below We Mocked UserRepository so that when we use getUser service of
		 * UserServiceImpl, it won't search/access database for data, rather it gets
		 * mocked stub obj created above because of the when condition written above
		 */
		UserDto userDto = userServiceImpl.getUser("asAnyStringIsMentionedAbove");

		assertNotNull(userDto);
		assertEquals("Roshan", userDto.getFirstName());
	}

	@Test // If it was jUnit 4 > @Test(expected = UsernameNotFoundException.class)
	void testGetUser_UsernameNotFoundException() {

		when(userRepository.findByEmail(anyString())).thenReturn(null);

		assertThrows (UsernameNotFoundException.class, () -> {
			userServiceImpl.getUser("asAnyStringIsMentionedAbove");
		});
	}
	
	@Test
	void testCreateUser_UserServiceException() {
		
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		assertThrows(UserServiceException.class, () -> {
			userServiceImpl.createUser(userDto);
		});
	}
	
	@Test
	final void testCreateUser() {
		
		//Mockito: when...then... type
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("hgfnghtyrir884");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(userPassword);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
		//Mockito: doNothing...when type
		Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDto.class));
 		


		UserDto storedUserDetails = userServiceImpl.createUser(userDto);
		//JUnit5: asserts
		assertNotNull(storedUserDetails);
		assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
		assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
		assertNotNull(storedUserDetails.getUserId());
		assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
		//Mockito: verify how many times specific method is called
		verify(utils,times(storedUserDetails.getAddresses().size())).generateAddressId(30);
		verify(bCryptPasswordEncoder, times(1)).encode("12345678");
		verify(userRepository,times(1)).save(any(UserEntity.class));
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
