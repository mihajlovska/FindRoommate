package com.roommate.api.domain;

import com.roommate.api.model.AppUser;
import com.roommate.api.payload.AppUserDAO;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

import java.io.IOException;

public class ConnectionSignUpImpl implements ConnectionSignUp {

	private AppUserDAO appUserDAO;

	public ConnectionSignUpImpl(AppUserDAO appUserDAO) {
		this.appUserDAO = appUserDAO;
	}

	// After logging in social networking.
	// This method will be called to create a corresponding App_User record
	// if it does not already exist.
	@Override
	public String execute(Connection<?> connection) {

		AppUser account = null;
		try {
			account = appUserDAO.createAppUser(connection);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assert account != null;
		return account.getUserName();
	}

}
