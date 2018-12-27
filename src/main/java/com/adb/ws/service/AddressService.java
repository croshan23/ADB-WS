package com.adb.ws.service;

import java.util.List;

import com.adb.ws.shared.dto.AddressDto;

public interface AddressService {

	List<AddressDto> getAddresses(String userId);
}
