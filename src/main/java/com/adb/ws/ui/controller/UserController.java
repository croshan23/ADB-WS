package com.adb.ws.ui.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adb.ws.exceptions.UserServiceException;
import com.adb.ws.service.AddressService;
import com.adb.ws.service.UserService;
import com.adb.ws.shared.dto.AddressDto;
import com.adb.ws.shared.dto.UserDto;
import com.adb.ws.ui.model.request.PasswordResetModel;
import com.adb.ws.ui.model.request.PasswordResetRequestModel;
import com.adb.ws.ui.model.request.UserDetailsRequestModel;
import com.adb.ws.ui.model.response.AddressesRest;
import com.adb.ws.ui.model.response.ErrorMessages;
import com.adb.ws.ui.model.response.OperationStatusModel;
import com.adb.ws.ui.model.response.RequestOperationName;
import com.adb.ws.ui.model.response.RequestOperationStatus;
import com.adb.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressService;
	
	/*
	 * Get user details based on ID
	 */
	@GetMapping(path="/{id}", 
			// Configure to return data in both xml and json
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
			)
	public UserRest getUser(@PathVariable String id) {
		
		UserRest returnValue = new UserRest();
		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnValue);
		return returnValue;
	}
	
	/*
	 * Creates new user 
	 */
	@PostMapping(
			// Configure to accept data in both xml and json
			consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE} ,
			// Configure to return data in both xml and json
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
			)
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception{
		
		UserRest returnValue = new UserRest();
		
		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		
		// BeanUtils doesn't work well while mapping objects inside objects
		// Hence, ModelMapper is used to perform the same task below.
			//UserDto userDto = new UserDto();
			//BeanUtils.copyProperties(userDetails, userDto);
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		
		UserDto createdUser = userService.createUser(userDto);
		returnValue = modelMapper.map(createdUser, UserRest.class);
		
		return returnValue;
	}
	
	/*
	 * Update existing user based on ID
	 */
	@PutMapping(path="/{id}",
			consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE} ,
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
			)
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		
		UserRest returnValue = new UserRest();
		
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		
		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);
		
		return returnValue;
	}
	
	/*
	 * Delete user based on ID
	 */
	@DeleteMapping(path="/{id}",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel deleteUser(@PathVariable String id) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(id);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	
	/*
	 * Get list of all users using pagination
	 */
	@GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public List<UserRest> getUsers(@RequestParam(value="page", defaultValue="0") int page,
			@RequestParam(value="limit", defaultValue="25") int limit){
		
		List<UserRest> returnValue = new ArrayList<>();
		
		List<UserDto> users = userService.getUsers(page, limit);
		
		for (UserDto dto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(dto, userModel);
			returnValue.add(userModel);
		}
		
		return returnValue;
	}
	
	/*
	 * Get shipping and billing address of a user based on ID
	 */
	@GetMapping(path="/{id}/addresses", 
			// Configure to return data in both xml and json
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"}
			)
	public Resources<AddressesRest> getUserAddresses(@PathVariable String id) {
		
		List<AddressesRest> addressListRestModel = new ArrayList<>();

		List<AddressDto> addressDto = addressService.getAddresses(id);
		
		if(addressDto != null && !addressDto.isEmpty()) {
			// This is a way provided my ModelMapper to map(copy) between List as well
			java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
			addressListRestModel = new ModelMapper().map(addressDto, listType);
			
			for(AddressesRest addressRest: addressListRestModel) {
				
				Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAdddressId())).withRel("address");
				addressRest.add(addressLink);
				Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
				addressRest.add(userLink);
			}
		}
		return new Resources<>(addressListRestModel);
	}
	
	/*
	 * Get specific address of a user based on user ID and address ID
	 */
	@GetMapping(path="/{userId}/addresses/{addressId}", 
			// Configure to return data in both xml and json
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"}
			)
	public Resource<AddressesRest> getUserAddress(@PathVariable String addressId, @PathVariable String userId) {
		
		AddressesRest returnValue = new AddressesRest();
		
		AddressDto addressDto = addressService.getAddress(addressId);
		ModelMapper modelMapper = new ModelMapper();
		
		returnValue = modelMapper.map(addressDto, AddressesRest.class);
		
		// Adding links to other API services using HATEOAS concept
		// Following are a way to create links but its lengthy. we are hardcoding everything with slash and all
			//Link selfAddressLink = linkTo(UserController.class).slash(userId).slash("addresses")
				//	.slash(addressId).withSelfRel();
		// Following does same thing like above code but with less code :)
		Link selfAddressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
		Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
		Link allAddressLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		
		returnValue.add(selfAddressLink);
		returnValue.add(userLink);
		returnValue.add(allAddressLink);
		
		return new Resource<>(returnValue);
	}
	
	/*
	 * Email Verification API
	 */
	@GetMapping(path="/email-verification", 
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/hal+json"}
			)
	public OperationStatusModel verifyEmailToken(@RequestParam(value="token") String token) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
		
		boolean isVerified = userService.verifyEmailToken(token);
		
		if (isVerified) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		else {
			returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		}
		
		return returnValue;
	}
	
	/*
	 * Password Reset Request Service
	 */
	@PostMapping(path = "/password-reset-request",
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public OperationStatusModel resetRequest(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		
		boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());
		
		returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if (operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		
		return returnValue;
	}
	
	/*
	 * Reset Password
	 */
	@PostMapping(path="/password-reset",
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		
		boolean operationResult = userService.resetPassword(
				passwordResetModel.getToken(), passwordResetModel.getPassword());
		
		returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if (operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		
		return returnValue;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
