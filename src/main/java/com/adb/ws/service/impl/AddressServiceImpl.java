package com.adb.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adb.ws.io.entity.AddressEntity;
import com.adb.ws.io.entity.UserEntity;
import com.adb.ws.repository.AddressRepository;
import com.adb.ws.repository.UserRepository;
import com.adb.ws.service.AddressService;
import com.adb.ws.shared.dto.AddressDto;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;
	
	@Override
	public List<AddressDto> getAddresses(String userId) {

		ModelMapper modelMapper = new ModelMapper();
		List<AddressDto> returnValue = new ArrayList<>();
		
		UserEntity userEntity = userRepository.findByUserId(userId);
		// yo pachi sidhai userEntity.getAddresses() method use gare sabai address pai halcha
		// but we are trying to use Spring JPA to get Addresses based on UserEntity by below codes
		
		if (userEntity == null) return returnValue;
		
		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
		
		for(AddressEntity addressEntity : addresses) {
			returnValue.add(modelMapper.map(addressEntity, AddressDto.class));
		}
		return returnValue;
	}

	@Override
	public AddressDto getAddress(String addressId) {

		AddressDto returnValue = new AddressDto();
		AddressEntity addressEntity = addressRepository.findByAdddressId(addressId);
		
		if (addressEntity != null) {
			returnValue = new ModelMapper().map(addressEntity, AddressDto.class);
		}
		return returnValue;
	}

}
