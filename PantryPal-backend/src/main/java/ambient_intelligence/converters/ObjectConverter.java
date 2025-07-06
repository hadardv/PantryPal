package ambient_intelligence.converters;

import org.springframework.stereotype.Component;

import ambient_intelligence.boundary.ObjectBoundary;
import ambient_intelligence.boundary.ObjectIdBoundary;
import ambient_intelligence.boundary.UserIdBoundary;
import ambient_intelligence.data.ObjectEntity;
import ambient_intelligence.service.PantryInvalidInputException;


@Component
public class ObjectConverter {
	public ObjectBoundary toBoundary(ObjectEntity entity) {
		ObjectBoundary boundary = new ObjectBoundary();
		ObjectIdBoundary objectId = new ObjectIdBoundary();
		UserIdBoundary userId = new UserIdBoundary();

		if (entity.getId() != null) {
			String[] IdParts = entity.getId().split("_");
			objectId.setObjectId(IdParts[0]);
			objectId.setSystemID(IdParts[1]);
			boundary.setId(objectId); 
		}
		
		if (entity.getCreationTimestamp() != null) {
			boundary.setCreationTimestamp(entity.getCreationTimestamp());
		}
		
		if(entity.getCreatedBy() != null) {
			String[] createdByParts = entity.getCreatedBy().split("_");
			userId.setSystemID(createdByParts[0]);
			userId.setEmail(createdByParts[1]);			
			boundary.setCreatedBy(userId);
		}
				
		boundary.setType(entity.getType());
		boundary.setAlias(entity.getAlias());
		boundary.setStatus(entity.getStatus());
		boundary.setActive(entity.isActive());		
		boundary.setObjectDetails(entity.getObjectDetails());
		
		return boundary;
	}
	
	
	public ObjectEntity toEntity(ObjectBoundary boundary) {
		ObjectEntity entity = new ObjectEntity();

		if (boundary.getId() != null) {
			try {
				entity.setId(boundary.getId().getObjectId() + "_" + boundary.getId().getSystemID());
			}catch(Exception e) {
				throw new PantryInvalidInputException("objectId is invalid");
			}
			
		}
		
		if (boundary.getCreationTimestamp() != null) {
			entity.setCreationTimestamp(boundary.getCreationTimestamp());
		}
		
		if(
				boundary.getCreatedBy() != null && 
				boundary.getCreatedBy().getEmail() != null &&
				boundary.getCreatedBy().getSystemID() != null){
			entity.setCreatedBy(boundary.getCreatedBy().getSystemID() + "_" + boundary.getCreatedBy().getEmail() );
		}
		
		if(boundary.getType() != null) {
			entity.setType(boundary.getType());
		}
				
		entity.setAlias(boundary.getAlias());
		entity.setStatus(boundary.getStatus());
		entity.setActive(boundary.getActive() != null ? boundary.getActive() : false);
		entity.setObjectDetails(boundary.getObjectDetails());
		
		return entity;
	}
}

