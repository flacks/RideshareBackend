package com.revature.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.maps.errors.ApiException;
import com.revature.models.Address;
import com.revature.models.User;
import com.revature.models.UserDTO;
import com.revature.services.DistanceService;
import com.revature.services.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * UserController takes care of handling our requests to /users.
 * It provides methods that can perform tasks like all users, user by role (true or false), user by username,
 * user by role and location, add user, update user and delete user by id. 
 * 
 * @author Adonis Cabreja
 *
 */

@RestController
@RequestMapping("/users")
@CrossOrigin
@Validated
@Api(tags = {"User"})
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private DistanceService distanceService;

	@ApiOperation(value="Returns user drivers", tags= {"User"})
	@GetMapping("/driver/{address}")
	public List <User> getTopFiveDrivers(@PathVariable("address")String address) throws ApiException, InterruptedException, IOException {
		List<String> destinationList = new ArrayList<>();
		String[] origins = {address};
		Map<String, User> topfive = new HashMap<>();
		for (User d : userService.getActiveDrivers()) {
			Address homeAddress = d.getHAddress();
			String fullAdd = String.format("%s %s, %s", homeAddress.getStreet(), homeAddress.getCity(), homeAddress.getState());
			destinationList.add(fullAdd);
			topfive.put(fullAdd, d);
		}
		String[] destinations = new String[destinationList.size()];
		destinations = destinationList.toArray(destinations);
		return distanceService.distanceMatrix(origins, destinations);
	}
	
	/**
	 * HTTP GET method (/users)
	 * 
	 * @param isDriver represents if the user is a driver or rider.
	 * @param username represents the user's username.
	 * @param location represents the batch's location.
	 * @return A list of all the users, users by is-driver, user by username and users by is-driver and location.
	 */
	@ApiOperation(value="Returns all users", tags= {"User"}, notes="Can also filter by is-driver, location and username")
	@GetMapping
	public List<User> getUsers(
			@RequestParam(name="is-driver", required=false)
			Boolean isDriver,

			//Prevents SQL and HTML injection by blocking <> and ;.
			@Pattern(regexp="[a-zA-Z0-9]", message="Username may only have letters and numbers.")
			@RequestParam(name="username", required=false)
			String username,

			//Prevents SQL and HTML injection by blocking <> and ;. Long term we will want to refactor as this long irregular string pushes RESTs requirements
			@Pattern(regexp = "[a-zA-Z0-9 ,]+", message = "Batch location may only contain letters, numbers, spaces, and commas")
			@RequestParam(name="location", required=false)
			String location
		) {

		if (isDriver != null && location != null) {
			return userService.getUserByRoleAndLocation(isDriver.booleanValue(), location);
		} else if (isDriver != null) {
			return userService.getUserByRole(isDriver.booleanValue());
		} else if (username != null) {
			return userService.getUserByUsername(username);
		}

		return userService.getUsers();
	}

	/**
	 * HTTP GET (users/{id})
	 *
	 * @param id represents the user's id.
	 * @return A user that matches the id.
	 */
	@ApiOperation(value = "Returns user by id", tags = {"User"})
	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(
			@Positive
			@PathVariable("id") 
			int id) {
		Optional<User> user = userService.getUserById(id);
		return user.map(value -> ResponseEntity.ok().body(value))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	/**
	 * HTTP POST method (/users)
	 *
	 * @param userDTO represents the new User object being sent.
	 * @return The newly created object with a 201 code.
	 * <p>
	 * Sends custom error messages when incorrect input is used
	 */
	@ApiOperation(value = "Adds a new user", tags = {"User"})
	@PostMapping
	public ResponseEntity<User> addUser(@Valid @RequestBody UserDTO userDTO) {
		User user = new User(userDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(user));
	}

	/**
	 * HTTP PUT method (/users)
	 *
	 * @param userDTO represents the updated User object being sent.
	 * @return The newly updated object.
	 */
	@ApiOperation(value = "Updates user by id", tags = {"User"})
	@PutMapping("/{id}")
	public User updateUser(@Valid @RequestBody UserDTO userDTO, @PathVariable String id) {
		User user = new User(userDTO);
		return userService.updateUser(user);
	}
	
	/**
	 * HTTP DELETE method (/users)
	 * 
	 * @param id represents the user's id.
	 * @return A string that says which user was deleted.
	 */
	@ApiOperation(value="Deletes user by id", tags= {"User"})
	@DeleteMapping("/{id}")
	public String deleteUserById(
			@Positive
			@PathVariable("id")
			int id) {

		return userService.deleteUserById(id);
	}
}
