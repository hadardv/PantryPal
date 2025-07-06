package ambient_intelligence.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort.Direction;

import ambient_intelligence.boundary.UserBoundary;
import ambient_intelligence.converters.UserConverter;
import ambient_intelligence.crud.UserCrud;
import ambient_intelligence.data.UserEntity;
import ambient_intelligence.data.UserRole;
import ambient_intelligence.logic.UsersService;
import ambient_intelligence.utils.*;

@Service
public class UserServiceImpl implements UsersService {

	private final UserCrud userCrud;
	private Log log = LogFactory.getLog(ObjectServiceImpl.class);
	private UserConverter userConverter;
	private String systemID;
	
	public UserServiceImpl(UserCrud userCrud, UserConverter userConverter) {
		this.userCrud = userCrud;
		this.userConverter = userConverter;
	}
	
	@Value("${spring.application.name:dummy}")
	public void setSystemID(String systemID) {
		this.systemID = systemID;
		log.info("*** " + this.systemID);
	}
	
	@Override
	@Transactional(readOnly = false) // default = not read only
	public UserBoundary createUser(UserBoundary userBoundary) {
		validateUserBoundary(userBoundary);
		
		String userId = userBoundary.getUserId().getSystemID() + "_" + userBoundary.getUserId().getEmail();
		if (userCrud.existsById(userId)) {
			throw new PantryInvalidInputException("User with email " + userBoundary.getUserId().getEmail() + " already exists.");
		}
				
		UserEntity entity = this.userConverter.toEntity(userBoundary);
		entity.setUserId(userId);
		
		return userConverter
				.toBoundary(this.userCrud.save(entity));
	}
		
	@Override
	@Transactional(readOnly = true)
	public Optional<UserBoundary> login(String systemID, String email) {		
		return this.userCrud
				.findById(systemID + "_" + email)
				.map(userConverter::toBoundary);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void updateUser(String systemID, String email, UserBoundary update) {
		
		if (!InputValidators.isValidEmail(email))
			throw new PantryInvalidInputException("Invalid email format");
		
		String id = systemID + "_" + email;
	    UserEntity existing = this.userCrud
	    		.findById(id)
	            .orElseThrow(() -> new PantryNotFoundException("Could not find user by credentials: " + email + " " + systemID));
	    
	    //check for no update at email and systemId fields
	    if (!update.getUserId().getEmail().equals(email)) {
	        throw new PantryInvalidInputException("E-mail cannot be changed");
	    }
	    if (!update.getUserId().getSystemID().equals(systemID)) {
	        throw new PantryInvalidInputException("System ID cannot be changed");
	    }
		if (update.getRole() == null || !InputValidators.isValidRole(update.getRole().toString())) {
		    throw new PantryInvalidInputException(
		            "Invalid role '" + update.getRole() +
		            "'. Valid roles are: " + Arrays.toString(UserRole.values()));

		}
		if (update.getUserName() == null || update.getUserName().isBlank()) {
			throw new PantryInvalidInputException("Invalid username");
		}
		if (update.getAvatar() == null || update.getAvatar().isBlank()) {
			throw new PantryInvalidInputException("Invalid avatar");
		}
	    
	    existing.setRole(update.getRole());
        existing.setUserName(update.getUserName());
        existing.setAvatar(update.getAvatar());

	    this.userCrud.save(existing);
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers(int size, int page, String systemId, String email) {
		String id = systemID + "_" + email;
	    UserEntity user = this.userCrud
	    		.findById(id)
	            .orElseThrow(() -> new PantryNotFoundException("Could not find user by credentials: " + email + " " + systemID));
	    
	    if(!user.getRole().equals(UserRole.ADMIN)) {
	    	throw new PantryUnauthorizedException("User in not authorized for the action");
	    }
	    List<UserEntity> entities = this.userCrud.findAll(PageRequest.of(page, size, Direction.ASC, "userId")).toList();
	    List<UserBoundary> userBoundaries = new ArrayList<>();
	    for (UserEntity entity : entities) {
	    	userBoundaries.add(this.userConverter.toBoundary(entity));
	    }
		return userBoundaries;
	}
	
	@Override
	@Transactional(readOnly = false)
	public void deleteAllUsers(String systemId, String email) {
		String id = systemID + "_" + email;
	    UserEntity user = this.userCrud
	    		.findById(id)
	            .orElseThrow(() -> new PantryNotFoundException("Could not find user by credentials: " + email + " " + systemID));
	    
	    if(!user.getRole().equals(UserRole.ADMIN)) {
	    	throw new PantryUnauthorizedException("User in not authorized for the action");
	    }
	    
		this.userCrud.deleteAll();
	}	
	
	private void validateUserBoundary(UserBoundary userBoundary) {
		if (userBoundary == null) {
			throw new PantryInvalidInputException("UserBoundary cannot be null");
		}
		if (!InputValidators.isValidEmail(userBoundary.getUserId().getEmail())) {
			throw new PantryInvalidInputException("Invalid email format");
		}
		if (userBoundary.getRole() == null || !InputValidators.isValidRole(userBoundary.getRole().toString())) {
		    throw new PantryInvalidInputException(
		            "Invalid role '" + userBoundary.getRole() +
		            "'. Valid roles are: " + Arrays.toString(UserRole.values()));

		}
		if (userBoundary.getUserName() == null || userBoundary.getUserName().isBlank()) {
			throw new PantryInvalidInputException("Invalid username");
		}
		if (userBoundary.getAvatar() == null || userBoundary.getAvatar().isBlank()) {
			throw new PantryInvalidInputException("Invalid avatar");
		}
	}

}
