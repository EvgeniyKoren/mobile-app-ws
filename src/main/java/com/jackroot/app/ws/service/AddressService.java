package com.jackroot.app.ws.service;

import java.util.List;

import com.jackroot.app.ws.shared.dto.AddressDTO;

public interface AddressService {
	
	List<AddressDTO> getAddresses(String userId);

	AddressDTO getAddress(String addressId);
}
