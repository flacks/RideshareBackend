package com.revature.models;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {

	private int userId;

	@NotBlank(message="Username cannot be blank.")
	@Size(min=3,max=12, message="Number of characters must be between 3 and 12.")
	@Pattern(regexp="[a-zA-Z0-9]", message="Username may only have letters and numbers.")
	private String userName;

	@Valid
	@NotNull
	private BatchDTO batch;

	@NotBlank(message="First name cannot be blank.")
	@Size(max=30, message= "Number of characters cannot be larger than 30.")
	@Pattern(regexp="^[a-zA-Z\\u00C0-\\u017F]+[- ]?[a-zA-Z\\u00C0-\\u017F]+$", message="First name format is incorrect")
	private String firstName;

	@NotBlank(message="Last name cannot be blank.")
	@Size(max=30, message="Number of characters cannot be larger than 30.")
	@Pattern(regexp="^[a-zA-Z\\u00C0-\\u017F]+[- ]?[a-zA-Z\\u00C0-\\u017F]+$", message="Last name format is incorrect")
	private String lastName;

	@NotBlank(message="Email cannot be blank.")
	@Email(message="Email format is incorrect.")
	private String email;

	@NotBlank(message="Phone number cannot be blank.")
	@Pattern(regexp = "^\\d{3}-\\d{3}-\\d{4}$", message="Phone number format is incorrect.")
	private String phoneNumber;

	private boolean isDriver;

	private boolean isActive;

	private boolean isAcceptingRides;

	private AddressDTO hAddress;

	private AddressDTO wAddress;

	public UserDTO(User user) {
		super();
		if (user != null) {
			this.userId = user.getUserId();
			this.userName = user.getUserName();
			this.batch = new BatchDTO(user.getBatch());
			this.firstName = user.getFirstName();
			this.lastName = user.getLastName();
			this.email = user.getEmail();
			this.phoneNumber = user.getPhoneNumber();
			this.isDriver = user.isDriver();
			this.isActive = user.isActive();
			this.isAcceptingRides = user.isAcceptingRides();
			this.hAddress = new AddressDTO(user.getHAddress());
			this.wAddress = new AddressDTO(user.getWAddress());
		}
	}
}
