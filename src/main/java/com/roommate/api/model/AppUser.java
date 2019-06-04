package com.roommate.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "App_User", uniqueConstraints = { @UniqueConstraint(name = "APP_USER_UK", columnNames = "User_Name"), @UniqueConstraint(name = "APP_USER_UK2", columnNames = "Email") })
public class AppUser {

	@Id
	@GeneratedValue
	@Column(name = "User_Id", nullable = false)
	private Long userId;

	@Column(name = "User_Name", length = 36, nullable = false)
	private String userName;

	@Column(name = "Email", length = 128, nullable = false)
	private String email;

	@Column(name = "First_Name", length = 36)
	private String firstName;

	@Column(name = "Last_Name", length = 36)
	private String lastName;

	@Column(name = "Encryted_Password", length = 128, nullable = false)
	private String encrytedPassword;

	@Column(name = "Enabled", length = 1, nullable = false)
	private boolean enabled;

	@Column(name="Interest",nullable = false)
	private String interest;

	@Column(name="Current_Location")
	private String currentLocation;

	@Column(name = "Next_Destination")
	private String nextDestination;

	@Column(name = "Education")
	private String education;

	private String userProviderID;
	private String userImage;


	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEncrytedPassword() {
		return encrytedPassword;
	}

	public void setEncrytedPassword(String encrytedPassword) {
		this.encrytedPassword = encrytedPassword;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getInterest() {
		return interest;
	}

	public void setInterests(String interest) {
		this.interest = interest;
	}

	public String getUserProviderID() {
		return userProviderID;
	}

	public void setUserProviderID(String userProviderID) {
		this.userProviderID = userProviderID;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

	public String getNextDestination() {
		return nextDestination;
	}

	public void setNextDestination(String nextDestination) {
		this.nextDestination = nextDestination;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}
}