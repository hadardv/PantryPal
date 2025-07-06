package ambient_intelligence.service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ambient_intelligence.boundary.ObjectBoundary;
import ambient_intelligence.boundary.ObjectIdBoundary;
import ambient_intelligence.converters.ObjectConverter;
import ambient_intelligence.crud.ObjectCrud;
import ambient_intelligence.crud.UserCrud;
import ambient_intelligence.data.ObjectEntity;
import ambient_intelligence.data.UserEntity;
import ambient_intelligence.data.UserRole;
import ambient_intelligence.enums.ObjectTypeEnum;
import ambient_intelligence.logic.ObjectService;
import ambient_intelligence.utils.*;

import org.springframework.data.domain.Pageable;
import java.util.Arrays;
import java.util.Date;


@Service
public class ObjectServiceImpl implements ObjectService{
	private ObjectCrud objectCrud;
	private ObjectConverter objectConverter;
	private String systemID;
	private UserCrud userCrud;
	private Log log = LogFactory.getLog(ObjectServiceImpl.class);	

	public ObjectServiceImpl(
			ObjectCrud objectCrud,
			ObjectConverter objectConverter,
			UserCrud userCrud
			) {
		this.objectCrud = objectCrud;
		this.objectConverter = objectConverter;
		this.userCrud = userCrud;
	}
	
	@Value("${spring.application.name:dummy}")
	public void setSystemID(String systemID) {
		this.systemID = systemID;
		this.log.info("***" + this.systemID);
	}
	
	@Override
	@Transactional(readOnly = false) // defualt = not read only
	public ObjectBoundary create(ObjectBoundary object) {
		validateObjectBoundary(object);
		UserEntity user= checkUserEntityExists(object.getCreatedBy().getSystemID(), object.getCreatedBy().getEmail());
		
	    if(!user.getRole().equals(UserRole.OPERATOR)) {
	    	throw new PantryUnauthorizedException("User in not authorized for the action");
	    }
			    
		ObjectIdBoundary objectId = new ObjectIdBoundary();
		objectId.setObjectId(UUID.randomUUID().toString());
		objectId.setSystemID(this.systemID);
		object.setId(objectId);
		
		object.setCreationTimestamp(new Date());
		object.getCreatedBy().setSystemID(systemID);
		
		ObjectEntity entity = this.objectConverter.toEntity(object);		
		
		ObjectBoundary rv = objectConverter.toBoundary(this.objectCrud.save(entity));
		this.log.trace("**** created: " + rv);
		return rv;

	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getAllObjects(String userSystemID, String userEmail, int size, int page) {
		checkValidPagingInput(size, page);

	    UserEntity user = checkUserEntityExists(userSystemID, userEmail);
	    Pageable paging = PageRequest.of(page, size, Direction.ASC,
	                                     "creationTimestamp", "id");

	    switch (user.getRole()) {
	        case OPERATOR:
	            return objectCrud.findAll(paging)                  
	                              .stream()
	                              .map(objectConverter::toBoundary)
	                              .toList();

	        case END_USER:
	            return objectCrud.findAllByActiveIsTrue(paging)         
	                              .stream()
	                              .map(objectConverter::toBoundary)
	                              .toList();

	        default:
	            throw new PantryUnauthorizedException(
	                    "Role '" + user.getRole() +
	                    "' is not authorised for this action");
	    }
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ObjectBoundary> getSpecificObject(String systemID, String id, String userSystemID, String userEmail) {
		UserEntity user = checkUserEntityExists(userSystemID, userEmail);
	    String objectId = id + "_" + systemID;
	    
	    switch (user.getRole()) {
	        case OPERATOR:
	            return Optional.of(
	                objectCrud.findById(objectId)
	                          .map(objectConverter::toBoundary)
	                          .orElseThrow(() -> new PantryNotFoundException(
	                                  "Could not find objectId: " + objectId)));
	
	        case END_USER:
	            return Optional.of(
	                objectCrud.findByIdAndActiveIsTrue(objectId)
	                          .map(objectConverter::toBoundary)
	                          .orElseThrow(() -> new PantryNotFoundException(
	                                  "Could not find objectId: " + objectId)));
	
	        default:
	            throw new PantryUnauthorizedException(
	                    "Role '" + user.getRole() +
	                    "' is not authorised to fetch this object");
		    }
	}

	@Override
	@Transactional(readOnly = false)
	public void updateObject(String systemID, String id, ObjectBoundary update, String userSystemId, String userEmail) {	
		String objectId = id + "_" + systemID;
	    ObjectEntity existing = this.objectCrud
	    		.findById(objectId)
	            .orElseThrow(() -> new PantryNotFoundException("Could not find object by id: " + objectId));
	    
		UserEntity user= checkUserEntityExists(userSystemId, userEmail);	
	    if(!user.getRole().equals(UserRole.OPERATOR)) {
	    	throw new PantryUnauthorizedException("User in not authorized for the action");
	    }


		// id could not be updated
	    //createdBy could not be updated
		// createdAt could not be updated
		ObjectEntity temp = objectConverter.toEntity(update);
		
		 if (!update.getCreatedBy().getEmail().equals(existing.getCreatedBy().split("_")[1])) {
		        throw new PantryInvalidInputException("email cannot be changed");
		    }
		 
		 if (!update.getCreatedBy().getSystemID().equals(existing.getCreatedBy().split("_")[0])) {
		        throw new PantryInvalidInputException("System ID cannot be changed");
		    }

		if (update.getType() != null)
			existing.setType(temp.getType());

		if (update.getAlias() != null)
			existing.setAlias(temp.getAlias());
	    
		if (update.getActive() != null) {
			existing.setActive(temp.isActive());
		}		
						
		 
		if(update.getObjectDetails() != null) {
			existing.setObjectDetails(update.getObjectDetails());
		}

		if(update.getStatus() != null) {
			existing.setStatus(update.getStatus());
		}
		
		this.objectCrud.save(existing);

	}
	
	@Override
	@Transactional(readOnly = false)
	public void bindObjects(
			String parentSystemID,
			String parentObjectID,
			String childSystemID,
			String childObjectID,
			String userSystemID,
			String userEmail) {
		UserEntity user= checkUserEntityExists(userSystemID, userEmail);	
		if(!userSystemID.equals(parentSystemID)) {
			throw new PantryUnauthorizedException("User in not authorized to bind objects from a different system");
		}
		
	    if(!user.getRole().equals(UserRole.OPERATOR)) {
	    	throw new PantryUnauthorizedException("User in not authorized for the action");
	    }
		
		// get parent entity by parentId if exists
		// otherwise return 404 status
		String parentId = parentObjectID + "_" + parentSystemID;
		ObjectEntity parent = this.objectCrud
			.findById(parentId)
			.orElseThrow(()->new PantryNotFoundException("Parent entity with id: " + parentObjectID + " not found"));		
		
		// get parent entity by childId if exists
		// otherwise return 404 status
		String childId = childObjectID + "_" + childSystemID;
		ObjectEntity child = this.objectCrud
				.findById(childId)
				.orElseThrow(()->new PantryNotFoundException("Child entity with id: " + childObjectID + " not found"));
				
		child.getParents().add(parent);
		parent.getChildren().add(child);

		this.objectCrud.save(child);
		this.objectCrud.save(parent);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getParents(
			String childSystemId, 
			String childObjectId,
			String userSystemID,
			String userEmail,
			int size,
			int page){
		
		checkValidPagingInput(size, page);
		InputValidators.isValidSystemId(this.systemID, userSystemID);
		UserEntity user = checkUserEntityExists(userSystemID, userEmail);
		
		if(!userSystemID.equals(childSystemId)) {
			throw new PantryUnauthorizedException("User in not authorized to see parents of a child from a different system");
		}


		String childId = childObjectId + "_" + childSystemId;
	    ObjectEntity child = objectCrud.findById(childId)
	            .orElseThrow(() -> new PantryNotFoundException(
	                    "Child entity with id: " + childObjectId + " not found"));
	    
	    switch (user.getRole()) {
	        case OPERATOR:
	        	return objectCrud
	        	.findByChildren_Id(
	        			childId,
	        			PageRequest.of(page, size,
                                Direction.ASC, "creationTimestamp", "id"))
	        	.stream()
	        	.map(objectConverter::toBoundary)
                .toList();


	        case END_USER:
	        	if (!child.isActive()) {
	                throw new PantryNotFoundException(
	                    "Child entity with id: " + childId + " not found");
	            }
	        	return objectCrud
	        	.findByChildren_IdAndActiveTrue(
	        			childId,
	        			PageRequest.of(page, size,
                                Direction.ASC, "creationTimestamp", "id"))
	        	.stream()
	        	.map(objectConverter::toBoundary)
                .toList();
	        	
	        default:
	            throw new PantryUnauthorizedException(
	                    "Role '" + user.getRole() + "' is not authorized to see parents");
	    }	    
	}
	    
	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getChildren(
			String parentSystemId,
			String parentObjectId,
			String userSystemID,
			String userEmail,
			int size,
			int page){
		
		checkValidPagingInput(size, page);
		InputValidators.isValidSystemId(this.systemID, userSystemID);
		UserEntity user = checkUserEntityExists(userSystemID, userEmail);
		
		if(!userSystemID.equals(parentSystemId)) {
			throw new PantryUnauthorizedException("User in not authorized to see children of a parent from a different system");
		}


		String parentId = parentObjectId + "_" + parentSystemId;
	    ObjectEntity parent = objectCrud.findById(parentId)
	            .orElseThrow(() -> new PantryNotFoundException(
	                    "Parent entity with id: " + parentObjectId + " not found"));
	    	    
	    switch (user.getRole()) {
	        case OPERATOR:
	        	return objectCrud
	        	.findByParents_Id(
	        			parentId,
	        			PageRequest.of(page, size,
                                Direction.ASC, "creationTimestamp", "id"))
	        	.stream()
	        	.map(objectConverter::toBoundary)
                .toList();


	        case END_USER:
	        	if (!parent.isActive()) {
	                throw new PantryNotFoundException(
	                    "Parent entity with id: " + parentId + " not found");
	            }

	        	return objectCrud
	        	.findByParents_IdAndActiveTrue(
	        			parentId,
	        			PageRequest.of(page, size,
                                Direction.ASC, "creationTimestamp", "id"))
	        	.stream()
	        	.map(objectConverter::toBoundary)
                .toList();
	        	
	        default:
	            throw new PantryUnauthorizedException(
	                    "Role '" + user.getRole() + "' is not authorized to see parents");
	    }	    
	}
	
	@Override
	@Transactional(readOnly = false)
	public void deleteAllObjects(String userSystemID, String userEmail) {
		UserEntity user= checkUserEntityExists(userSystemID, userEmail);
		
	    if(!user.getRole().equals(UserRole.ADMIN)) {
	    	throw new PantryUnauthorizedException("User in not authorized for the action");
	    }
	    
		objectCrud.deleteAll();
	}
	

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> searchByAlias(
			String alias,
			int size,
			int page,
			String userSystemID,
			String userEmail) {
		
		checkValidPagingInput(size, page);
		InputValidators.isValidSystemId(this.systemID, userSystemID);
		UserEntity user= checkUserEntityExists(userSystemID,userEmail);

		switch(user.getRole()) {
	        case OPERATOR:
	    		return objectCrud
	    				.findAllByAlias(
	    						alias,
	    						PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "id"))
	    				.stream() // Stream<ObjectEntity>
	    				.map(this.objectConverter::toBoundary) // Stream<ObjectBoundary>
	    				.toList(); // List<ObjectBoundary>	

	
	        case END_USER:
	    		return objectCrud
	    				.findAllByAliasAndActiveIsTrue(
	    						alias,
	    						PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "id"))
	    				.stream() // Stream<ObjectEntity>
	    				.map(this.objectConverter::toBoundary) // Stream<ObjectBoundary>
	    				.toList(); // List<ObjectBoundary>	
	        	
	        default:
	            throw new PantryUnauthorizedException(
	                    "Role '" + user.getRole() + "' is not authorized to search by alias");
	    }	    
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> searchByAliasPattern(
			String pattern,
			int size,
			int page,
			String userSystemID,
			String userEmail) {
		
		checkValidPagingInput(size, page);
		InputValidators.isValidSystemId(this.systemID, userSystemID);
		UserEntity user= checkUserEntityExists(userSystemID,userEmail);
		
		switch(user.getRole()) {
	        case OPERATOR:
	    		return objectCrud
	    				.findAllByAliasLike(
	    						"*" + pattern + "*",
	    						PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "id"))
	    				.stream() // Stream<ObjectEntity>
	    				.map(this.objectConverter::toBoundary) // Stream<ObjectBoundary>
	    				.toList(); // List<ObjectBoundary>	
	
	
	        case END_USER:
	    		return objectCrud
	    				.findAllByAliasLikeAndActiveIsTrue(		
	        					"*" + pattern + "*",
	    						PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "id"))
	    				.stream() // Stream<ObjectEntity>
	    				.map(this.objectConverter::toBoundary) // Stream<ObjectBoundary>
	    				.toList(); // List<ObjectBoundary>	
	        	
	        default:
	            throw new PantryUnauthorizedException(
	                    "Role '" + user.getRole() + "' is not authorized to search by alias pattern");
	    }	    
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> searchByType(
			ObjectTypeEnum type,
			int size,
			int page,
			String userSystemID,
			String userEmail) {
		
		checkValidPagingInput(size, page);
		InputValidators.isValidSystemId(this.systemID, userSystemID);
		UserEntity user= checkUserEntityExists(userSystemID,userEmail);

		switch(user.getRole()) {
	        case OPERATOR:
	    		return this.objectCrud
	    				.findAllByType(
	    						type,
	    						PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "id"))
	    				.stream() // Stream<ObjectEntity>
	    				.map(this.objectConverter::toBoundary) // Stream<ObjectBoundary>
	    				.toList(); // List<ObjectBoundary>		
	
	        case END_USER:
	    		return this.objectCrud
	    				.findAllByTypeAndActiveIsTrue(		
	    						type,
	    						PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "id"))
	    				.stream() // Stream<ObjectEntity>
	    				.map(this.objectConverter::toBoundary) // Stream<ObjectBoundary>
	    				.toList(); // List<ObjectBoundary>		
	        	
	        default:
	            throw new PantryUnauthorizedException(
	                    "Role '" + user.getRole() + "' is not authorized to search by type");
	    }	    
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> searchByStatus(
			String status,
			int size,
			int page,
			String userSystemID,
			String userEmail) {
		
		checkValidPagingInput(size, page);
		InputValidators.isValidSystemId(this.systemID, userSystemID);
		UserEntity user= checkUserEntityExists(userSystemID,userEmail);
		
		switch(user.getRole()) {
	        case OPERATOR:
				return this.objectCrud
						.findAllByStatus(
								status,
								PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "id"))
						.stream() // Stream<ObjectEntity>
						.map(this.objectConverter::toBoundary) // Stream<ObjectBoundary>
						.toList(); // List<ObjectBoundary>	

	
	        case END_USER:
				return this.objectCrud
						.findAllByStatusAndActiveIsTrue(
								status,
								PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "id"))
						.stream() // Stream<ObjectEntity>
						.map(this.objectConverter::toBoundary) // Stream<ObjectBoundary>
						.toList(); // List<ObjectBoundary>	
	        	
	        default:
	            throw new PantryUnauthorizedException(
	                    "Role '" + user.getRole() + "' is not authorized to search by status");
	    }	    
	}
		    		   

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> searchByTypeAndStatus(
			ObjectTypeEnum type,
			String status,
			int size,
			int page,
			String userSystemID,
			String userEmail) {
		
		checkValidPagingInput(size, page);
		InputValidators.isValidSystemId(this.systemID, userSystemID);
		UserEntity user= checkUserEntityExists(userSystemID,userEmail);
		
		switch(user.getRole()) {
	        case OPERATOR:
	    		return this.objectCrud
	    				.findAllByTypeAndStatus(
	    						type,
	    						status,
	    						PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "id"))
	    				.stream() // Stream<ObjectEntity>
	    				.map(this.objectConverter::toBoundary) // Stream<ObjectBoundary>
	    				.toList(); // List<ObjectBoundary>	

	
	        case END_USER:
				return this.objectCrud
						.findAllByTypeAndStatusAndActiveIsTrue(
	    						type,
	    						status,
	    						PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "id"))
	    				.stream() // Stream<ObjectEntity>
	    				.map(this.objectConverter::toBoundary) // Stream<ObjectBoundary>
	    				.toList(); // List<ObjectBoundary>	
	        	
	        default:
	            throw new PantryUnauthorizedException(
	                    "Role '" + user.getRole() + "' is not authorized to search by type and status");
	    }	    
	}

	private UserEntity checkUserEntityExists(String systemID, String email) {    
	    String userId = systemID + "_" + email;
	    log.debug("Looking for userId: " + userId);
	    
	    UserEntity user = this.userCrud
	        .findById(userId)
	        .orElseThrow(() -> new PantryNotFoundException("Could not find user by credentials: " + email + " " + systemID));
	    
	    return user;
	}
	

	public void checkValidPagingInput(int size, int page) {
		
	    if (page < 0) {
	        throw new PantryInvalidInputException("Page number must be non-negative. Received: " + page);
	    }
	    
	    if (size < 1) {
	        throw new PantryInvalidInputException("Page size must be at least 1. Received: " + size);
	    }
	}
	
	private void validateObjectBoundary(ObjectBoundary objectBoundary) {
		if (objectBoundary == null) {
			throw new PantryInvalidInputException("ObjectBoundary cannot be null.");
		}
		
		if (
				objectBoundary.getActive() == null) {
			throw new PantryInvalidInputException("active cannot be null");
		}

		if (
				objectBoundary.getType() == null ||
				objectBoundary.getType().toString().isBlank() ||
				!InputValidators.isValidType(objectBoundary.getType().toString())) {
		    throw new PantryInvalidInputException(
		            "Invalid type '" + objectBoundary.getType() +
		            "'. Valid types are: " + Arrays.toString(ObjectTypeEnum.values()));
		}
		
		if (objectBoundary.getAlias() == null || objectBoundary.getAlias().isBlank()) {
			throw new PantryInvalidInputException("alias cannot be null or empty");
		}

		if (
				objectBoundary.getCreatedBy() == null ||
				!InputValidators.isValidEmail(objectBoundary.getCreatedBy().getEmail()) ||
				!InputValidators.isValidSystemId(systemID ,objectBoundary.getCreatedBy().getSystemID())
				) {
			throw new PantryInvalidInputException("CreatedBy is invalid");
		}
		
	    if (objectBoundary.getStatus() == null || objectBoundary.getStatus().isBlank()) {
	        throw new PantryInvalidInputException("status cannot be null or empty.");
	    }
	}
}
