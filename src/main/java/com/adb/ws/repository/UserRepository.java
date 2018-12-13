package com.adb.ws.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.adb.ws.io.entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> { 
	// this is used to implement paging and sorting, not provided by CrudRepo
	// Now we can get methods that will get data based on limits eg. get all 25 items.
	//CrudRepository<UserEntity, Long> { // was before

	// Spring Data JPA help to write methods to get data from database
	// Here, find and By is a keyword to search record 
	// Email is the entity variable/database column by which it will find
	// If we were finding by userId, we would write findByUserId
	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId);
}
