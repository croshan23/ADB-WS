package com.adb.ws.repository;

import org.springframework.data.repository.CrudRepository;

import com.adb.ws.io.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long>{

	PasswordResetTokenEntity findByToken(String token);

	
}
