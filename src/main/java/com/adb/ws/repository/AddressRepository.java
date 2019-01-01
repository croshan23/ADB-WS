package com.adb.ws.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.adb.ws.io.entity.AddressEntity;
import com.adb.ws.io.entity.UserEntity;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long>{

	//Here find is a keyword for JPA to know its find task
	//Similarly, All mean all, By , UserDetails is a variable in AddressEntity
	Iterable<AddressEntity> findAllByUserDetails(UserEntity userEntity);

	AddressEntity findByAdddressId(String addressId);

}
