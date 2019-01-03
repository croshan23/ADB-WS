package com.adb.ws.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
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
	
	@DeleteMapping(path="/{id}",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel deleteUser(@PathVariable String id) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(id);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
