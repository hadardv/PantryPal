package ambient_intelligence.converters;

import org.springframework.stereotype.Component;

import ambient_intelligence.boundary.CommandBoundary;
import ambient_intelligence.boundary.CommandIdBoundary;
import ambient_intelligence.boundary.ObjectIdBoundary;
import ambient_intelligence.boundary.UserIdBoundary;
import ambient_intelligence.data.CommandEntity;
import ambient_intelligence.service.PantryInvalidInputException;

@Component
public class CommandConverter {
	
	public CommandBoundary toBoundary(CommandEntity commandEntity) {
		CommandBoundary newCommandBoundary = new CommandBoundary();
		CommandIdBoundary  commandId = new CommandIdBoundary();
		ObjectIdBoundary objectId = new ObjectIdBoundary();
		UserIdBoundary userId = new UserIdBoundary();
		
		if (commandEntity.getCommandId() != null) {
			String[] commandIdParts = commandEntity.getCommandId().split("_");
			if (commandIdParts.length != 2) {
				throw new IllegalArgumentException("Invalid command ID format");
			}
			commandId.setCommandId(commandIdParts[0]);
			commandId.setSystemID(commandIdParts[1]);
			newCommandBoundary.setCommandId(commandId);
		}

		newCommandBoundary.setCommand(commandEntity.getCommand());
		newCommandBoundary.setInvocationTimestamp(commandEntity.getInvocationTimestamp());
		
		if(commandEntity.getTargetObject() != null) {
			String[] targetObjectParts = commandEntity.getTargetObject().split("_");
			if (targetObjectParts.length == 2) {
				objectId.setSystemID(targetObjectParts[1]);
				objectId.setObjectId(targetObjectParts[0]);			
				newCommandBoundary.setTargetObject(objectId);
			}
		}

		if(commandEntity.getInvokedBy() != null) {
			String[] invokedByParts = commandEntity.getInvokedBy().split("_");
			if (invokedByParts.length == 2) {
				userId.setEmail(invokedByParts[1]);
				userId.setSystemID(invokedByParts[0]);
				newCommandBoundary.setInvokedBy(userId);
			}
		}
		
		newCommandBoundary.setCommandAttributes(commandEntity.getCommandAttributes()); 
		return newCommandBoundary;

	}
	
	public CommandEntity toEntity(CommandBoundary commandBoundary) {		
		CommandEntity newCommandEntity = new CommandEntity();
		
		if(commandBoundary.getCommandId() != null) {			
			try {
				
				newCommandEntity.setCommandId(String.join("_", commandBoundary.getCommandId().getCommandId(),
						commandBoundary.getCommandId().getSystemID()));
			} catch (Exception e) {
				throw new PantryInvalidInputException("commandId or commandId components are invalid");
			}
			
		} 		
		
		if(commandBoundary.getCommand() != null) {
			newCommandEntity.setCommand(commandBoundary.getCommand());
		} 
		
		if(commandBoundary.getTargetObject() != null) {
			
			try {
				newCommandEntity.setTargetObject(
						String.join("_", commandBoundary.getTargetObject().getObjectId(), commandBoundary.getTargetObject().getSystemID()));
			} catch (Exception e) {
				throw new PantryInvalidInputException("targetObject or targetObject components are invalid");
			}
		}
		
		newCommandEntity.setInvocationTimestamp(commandBoundary.getInvocationTimestamp());
		
		if(commandBoundary.getInvokedBy() != null) {
			newCommandEntity.setInvokedBy(String.join("_", commandBoundary.getInvokedBy().getSystemID(), commandBoundary.getInvokedBy().getEmail()));
		}
		
		if(commandBoundary.getCommandAttributes() != null) {
			newCommandEntity.setCommandAttributes(commandBoundary.getCommandAttributes());
		} 
				
		return newCommandEntity;
		
	}

}
