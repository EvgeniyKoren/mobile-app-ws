package com.jackroot.app.ws.ui.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jackroot.app.ws.service.AddressService;
import com.jackroot.app.ws.service.UserService;
import com.jackroot.app.ws.shared.Roles;
import com.jackroot.app.ws.shared.dto.AddressDTO;
import com.jackroot.app.ws.shared.dto.UserDto;
import com.jackroot.app.ws.ui.model.request.PasswordResetModel;
import com.jackroot.app.ws.ui.model.request.PasswordResetRequestModel;
import com.jackroot.app.ws.ui.model.request.UserDetailsRequestModel;
import com.jackroot.app.ws.ui.model.response.AddressesRest;
import com.jackroot.app.ws.ui.model.response.OperationStatusModel;
import com.jackroot.app.ws.ui.model.response.RequestOperationStatus;
import com.jackroot.app.ws.ui.model.response.UserRest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/users") // http://localhost:8080/users
//@CrossOrigin(origins= {"http://localhost:8083", "http://localhost:8084"})
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressesService;

	@PostAuthorize("hasRole('ROLE_ADMIN') or returnObject.userId == principal.userId")
	@ApiOperation(value = "The Get User Details Web Service Endpoint",
			notes = "${userController.GetUser.ApiOperation.Notes}")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", 
						  value = "${userController.authorizationHeader.description}", 
						  paramType = "header")
	})
	@GetMapping(path = "/{id}",
				produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest getUser(@PathVariable String id) {
		UserRest returnValue = new UserRest();
		
		UserDto userDto = userService.getUserByUserId(id);
//		BeanUtils.copyProperties(userDto, returnValue);
		ModelMapper modelMapper = new ModelMapper();
		returnValue = modelMapper.map(userDto, UserRest.class);
		
		return returnValue;
	}

	@ApiOperation(value = "The Create User Web Service Endpoint")
	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, 
				 produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		UserRest returnValue = new UserRest();
		
//		UserDto userDto = new UserDto();
//		BeanUtils.copyProperties(userDetails, userDto);
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name())));

		UserDto createdUser = userService.createUser(userDto);
		returnValue = modelMapper.map(createdUser, UserRest.class);
		
		return returnValue;
	}

	@ApiOperation(value = "The Update User Web Service Endpoint")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", 
						  value = "${userController.authorizationHeader.description}", 
						  paramType = "header")
	})
	@PutMapping(path = "/{id}",
				consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, 
			 	produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserRest returnValue = new UserRest();

		UserDto userDto = new UserDto();
		userDto = new ModelMapper().map(userDetails, UserDto.class);

		UserDto updatedUser = userService.updateUser(id, userDto);
		returnValue = new ModelMapper().map(updatedUser, UserRest.class);

		return returnValue;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or #id == principal.userId")
//	@PreAuthorize("hasAuthority('DELETE_AUTHORITY')")
//	@Secured("ROLE_ADMIN")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", 
						  value = "${userController.authorizationHeader.description}", 
						  paramType = "header")
	})
	@DeleteMapping(path = "/{id}",
				   produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(id);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", 
						  value = "${userController.authorizationHeader.description}", 
						  paramType = "header")
	})
	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
								   @RequestParam(value = "limit", defaultValue = "2") int limit) {
		List<UserRest> returnValue = new ArrayList<>();
		List<UserDto> users = userService.getUsers(page, limit);
		
		Type listType = new TypeToken<List<UserRest>>() {}.getType();
		returnValue = new ModelMapper().map(users, listType);
		
		/*
		 * for (UserDto userDto : users) { UserRest userModel = new UserRest();
		 * BeanUtils.copyProperties(userDto, userModel); returnValue.add(userModel); }
		 */
		
		return returnValue;
	}
	
	//http://localhost:8080/mobile-app-ws/users/sdjkflsjljasdflj/addresses
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", 
						  value = "${userController.authorizationHeader.description}", 
						  paramType = "header")
	})
	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {
		List<AddressesRest> addressesListRestModel = new ArrayList<>();
		
		List<AddressDTO> addressesDTO = addressesService.getAddresses(id);
		
		if (addressesDTO != null && !addressesDTO.isEmpty()) {
			Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
			addressesListRestModel = new ModelMapper().map(addressesDTO, listType);
			
			for (AddressesRest addressRest : addressesListRestModel) {
				Link addressLink = linkTo(methodOn(UserController.class)
									.getUserAddress(id, addressRest.getAddressId())).withSelfRel();
				Link userLink = linkTo(methodOn(UserController.class)
									.getUser(id)).withRel("user");
				addressRest.add(addressLink);
				addressRest.add(userLink);
			}
		}
		
		return new CollectionModel<>(addressesListRestModel);
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", 
						  value = "${userController.authorizationHeader.description}", 
						  paramType = "header")
	})
	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
		
		AddressDTO addressDto = addressesService.getAddress(addressId);
		ModelMapper modelMaper = new ModelMapper();
		Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
		Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
		Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		
		AddressesRest addressesRestModel = modelMaper.map(addressDto, AddressesRest.class);
		addressesRestModel.add(addressLink);
		addressesRestModel.add(userLink);
		addressesRestModel.add(addressesLink);
		
		return new EntityModel<>(addressesRestModel);
	}
	
	// http://localhost:8080/mobile-app-ws/users/email-verification?token=sdfsdf
	@GetMapping(path = "/email-verification", 
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
		
		boolean isVerified = userService.verifyEmailToken(token);
		if (isVerified) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		} else {
			returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		}
		
		return returnValue;
	}
	
	// http://localhost:8080/mobile-app-ws/users/password-reset-request
	@PostMapping(path = "/password-reset-request", 
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
		OperationStatusModel returnValue = new OperationStatusModel();
		
		boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());
		
		returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if (operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		
		return returnValue;
	}
	
	@PostMapping(path = "/password-reset",
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
		OperationStatusModel returnValue = new OperationStatusModel();
		
		boolean operationResult = userService.resetPassword(
				passwordResetModel.getToken(), 
				passwordResetModel.getPassword());
		
		returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if (operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		
		return returnValue;
	}
}
