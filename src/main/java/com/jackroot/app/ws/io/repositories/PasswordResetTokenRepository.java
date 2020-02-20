package com.jackroot.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;

import com.jackroot.app.ws.io.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {

	PasswordResetTokenEntity findByToken(String token);

}