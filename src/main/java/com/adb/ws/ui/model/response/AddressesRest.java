package com.adb.ws.ui.model.response;

import org.springframework.hateoas.ResourceSupport;

public class AddressesRest extends ResourceSupport{

	private String adddressId;
	private String city;
	private String country;
	private String streetName;
	private String postalCode;
	private String type;
	
	public String getAdddressId() {
		return adddressId;
	}
	public void setAdddressId(String adddressId) {
		this.adddressId = adddressId;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
