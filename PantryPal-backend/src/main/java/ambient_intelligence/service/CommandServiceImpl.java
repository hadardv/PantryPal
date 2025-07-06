package ambient_intelligence.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ambient_intelligence.boundary.CommandBoundary;
import ambient_intelligence.boundary.CommandIdBoundary;
import ambient_intelligence.boundary.ObjectBoundary;
import ambient_intelligence.converters.CommandConverter;
import ambient_intelligence.converters.ObjectConverter;
import ambient_intelligence.crud.CommandCrud;
import ambient_intelligence.crud.ObjectCrud;
import ambient_intelligence.crud.UserCrud;
import ambient_intelligence.data.CommandEntity;
import ambient_intelligence.data.ObjectEntity;
import ambient_intelligence.data.UserEntity;
import ambient_intelligence.data.UserRole;
import ambient_intelligence.enums.ObjectTypeEnum;
import ambient_intelligence.logic.CommandService;

@Service
public class CommandServiceImpl implements CommandService {
	private Log log = LogFactory.getLog(ObjectServiceImpl.class);
	private final CommandCrud commandCrud;
	private String systemID;
	private CommandConverter commandConverter;
	private UserCrud userCrud;
	private ObjectCrud objectCrud;
    private final ObjectConverter objectConverter;


	public CommandServiceImpl(CommandCrud commandCrud, CommandConverter commandConverter, UserCrud userCrud,
			ObjectCrud objectCrud, ObjectConverter objectConverter) {
		this.commandCrud = commandCrud;
		this.commandConverter = commandConverter;
		this.userCrud = userCrud;
		this.objectCrud = objectCrud;
        this.objectConverter = objectConverter;

	}

	@Value("${spring.application.name:dummy}")
	public void setSystemID(String systemID) {
		this.systemID = systemID;
		log.info("*** " + this.systemID);
	}

	@Override
	@Transactional(readOnly = false)
	public List<Object> invokeCommand(CommandBoundary commandBoundary) {

		List<String> missingFields = new ArrayList<>();
		
		if (commandBoundary.getCommand() == null || commandBoundary.getCommand().isBlank()) {
			if (commandBoundary.getCommand() == null) {
				missingFields.add("command is required");
			}
			if (commandBoundary.getCommand().isBlank()) {
				missingFields.add("command should not be an empty String");
			}

		}
		if (!commandBoundary.getCommand().equals("getProductsExpiringInNextWeek")
			    && !commandBoundary.getCommand().equals("getExpiredOrOutOfStockProducts")) {
			if (commandBoundary.getTargetObject() == null) {
				missingFields.add("targetObject is required");
			} else {
				if (commandBoundary.getTargetObject().getObjectId() == null
						|| commandBoundary.getTargetObject().getObjectId().isBlank()) {
					missingFields.add("targetObject.objectId is required");
				}
				if (commandBoundary.getTargetObject().getSystemID() == null
						|| commandBoundary.getTargetObject().getSystemID().isBlank()) {
					missingFields.add("targetObject.systemID is required");
				}
			}
		}


		if (commandBoundary.getInvokedBy() == null) {
			missingFields.add("invokedBy is required");
		} else {
			if (commandBoundary.getInvokedBy().getEmail() == null
					|| commandBoundary.getInvokedBy().getEmail().isBlank()) {
				missingFields.add("invokedBy.email is required");
			}
			if (commandBoundary.getInvokedBy().getSystemID() == null
					|| commandBoundary.getInvokedBy().getSystemID().isBlank()) {
				missingFields.add("invokedBy.systemID is required");
			}
		}
		
		// Check page and size
		Object pageObj = commandBoundary.getCommandAttributes().getOrDefault("page", "0");
		Object sizeObj = commandBoundary.getCommandAttributes().getOrDefault("size", "100");

		int page, size;

		try {
			page = Integer.parseInt(pageObj.toString());
			if (page < 0) {
				missingFields.add("page must be >= 0");
			}
		} catch (NumberFormatException e) {
			missingFields.add("page must be a valid integer");
			page = 0; 
		}
		
		

		try {
			size = Integer.parseInt(sizeObj.toString());
			if (size <= 0) {
				missingFields.add("size must be > 0");
			}
		} catch (NumberFormatException e) {
			missingFields.add("size must be a valid integer");
			size = 10;
		}

		if (!missingFields.isEmpty()) {
			String errorMessage = "Missing required fields: " + String.join(", ", missingFields);
			throw new PantryInvalidInputException(errorMessage);
		}
		
		
		CommandIdBoundary commandIdBoundary = new CommandIdBoundary();
		commandIdBoundary.setCommandId(UUID.randomUUID().toString());
		commandIdBoundary.setSystemID(systemID);
		commandBoundary.setCommandId(commandIdBoundary);

		CommandEntity commandEntity = this.commandConverter.toEntity(commandBoundary);

		log.debug(commandEntity.getInvokedBy());

		UserEntity invokedBy = this.userCrud.findById(commandEntity.getInvokedBy())
				.orElseThrow(() -> new PantryNotFoundException(
						"Could not find user by credentials: " + commandEntity.getInvokedBy()));

		if (!invokedBy.getRole().equals(UserRole.END_USER)) {
			throw new PantryUnauthorizedException("User in not authorized for the action");
		}
		
		if (!commandBoundary.getCommand().equals("getProductsExpiringInNextWeek")
			    && !commandBoundary.getCommand().equals("getExpiredOrOutOfStockProducts")) {
			ObjectEntity targetObjectEntity = this.objectCrud.findById(commandEntity.getTargetObject())
					.orElseThrow(() -> new PantryNotFoundException(
							"Could not find targetObject by credentials: " + commandEntity.getTargetObject()));

			if (!targetObjectEntity.isActive()) {
				throw new PantryNotFoundException("tagetobject is not found");
			}
			}

		List<Object> returnValue = handleCommand(commandEntity, page, size);

		this.commandCrud.save(commandEntity);
		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommandBoundary> getAllCommandsHistory(int size, int page, String systemId, String email) {
		String id = systemID + "_" + email;
		log.debug(id);
		UserEntity user = this.userCrud.findById(id).orElseThrow(
				() -> new PantryNotFoundException("Could not find user by credentials: " + email + " " + systemID));

		if (!user.getRole().equals(UserRole.ADMIN)) {
			throw new PantryUnauthorizedException("User in not authorized for the action");
		}

		List<CommandEntity> entities = this.commandCrud.findAll(PageRequest.of(page, size, Direction.ASC, "commandId"))
				.toList();
		List<CommandBoundary> commandBoundaries = new ArrayList<>();
		for (CommandEntity entity : entities) {
			commandBoundaries.add(this.commandConverter.toBoundary(entity));
		}
		return commandBoundaries;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllCommands(String systemId, String email) {
		String id = systemID + "_" + email;
		UserEntity user = this.userCrud.findById(id).orElseThrow(
				() -> new PantryNotFoundException("Could not find user by credentials: " + email + " " + systemID));

		if (!user.getRole().equals(UserRole.ADMIN)) {
			throw new PantryUnauthorizedException("User in not authorized for the action");
		}
		this.commandCrud.deleteAll();

	}

	private List<Object> handleCommand(CommandEntity commandEntity, int page, int size) {

		// List<ObjectEntity> objectEntities = objectCrud.findAll();
		List<ObjectEntity> objectEntities = objectCrud.findAllByActiveIsTrue(PageRequest.
				of(page, size, Direction.DESC, "creationTimestamp", "id"));
		
		switch (commandEntity.getCommand()) {
		case "getExpiredOrOutOfStockProducts":
			return getExpiredOrOutOfStockProducts(objectEntities);

		case "getProductsExpiringInNextWeek":
			return getProductsExpiringInNextWeek(objectEntities);

		default:
			List<Object> response = new ArrayList<>();
			response.add("Command ID: " + commandEntity.getCommandId().split("_")[0]);
			response.add("System ID: " + commandEntity.getCommandId().split("_")[0]);
			response.add("command: " + commandEntity.getCommand());
			response.add("Command Attributes: " + commandEntity.getCommandAttributes());
			return response;
		}

	}

    public List<Object> getExpiredOrOutOfStockProducts(List<ObjectEntity> objectEntities) {
        List<Object> requiredProducts = new ArrayList<>();
        Date now = new Date();
        
        log.debug(objectEntities);

        for (ObjectEntity currentObject : objectEntities) {
        	log.debug(currentObject);
            ObjectTypeEnum type = currentObject.getType();

            if (type == ObjectTypeEnum.PRODUCT_BY_QUANTITY || type == ObjectTypeEnum.PRODUCT_BY_WEIGHT) {
                Map<String, Object> details = currentObject.getObjectDetails();
                if (details == null) {
                    continue;
                }

                Date expiration = null;
                Object expirationObj = details.get("expiration");
                if (expirationObj instanceof Date) {
                    expiration = (Date) expirationObj;
                }
                else if (expirationObj instanceof String) {
                    String expirationString = (String) expirationObj;
                    try {
                        // Convert “2025-05-31” → LocalDate → java.util.Date
                        LocalDate localDate = LocalDate.parse(
                            expirationString,
                            DateTimeFormatter.ISO_LOCAL_DATE
                        );
                        expiration = Date.from(
                            localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                        );
                    } catch (Exception e) {
                        log.error("Could not parse expiration date: " + expirationString);
                    }
                }

                Integer amount = null;
                Object amountObj = details.get("amount");
                if (amountObj instanceof Integer) {
                    amount = (Integer) amountObj;
                }
                else if (amountObj instanceof String) {
                    try {
                        amount = Integer.valueOf((String) amountObj);
                    } catch (NumberFormatException e) {
                    	log.error("Could not parse amount: " + amountObj);
                    }
                }

                // --- Check if expired or out of stock ---
                boolean isExpired   = (expiration != null && expiration.before(now));
                boolean isOutOfStock = (amount != null && amount <= 0);

                if (isExpired || isOutOfStock) {
                    // Convert the entity into its boundary (DTO) form,
                    // then add that boundary to the result list.
                    ObjectBoundary boundary = this.objectConverter.toBoundary(currentObject);
                    requiredProducts.add(boundary);
                }
            }
        }

        return requiredProducts;
    }

    private List<Object> getProductsExpiringInNextWeek(List<ObjectEntity> objectEntities) {
        List<Object> requiredProducts = new ArrayList<>();

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        Date oneWeekFromNow = calendar.getTime();

        for (ObjectEntity currentObject : objectEntities) {
            ObjectTypeEnum type = currentObject.getType();

            if (type == ObjectTypeEnum.PRODUCT_BY_QUANTITY || type == ObjectTypeEnum.PRODUCT_BY_WEIGHT) {
                Map<String, Object> details = currentObject.getObjectDetails();
                if (details == null) {
                    continue;
                }

                // Parse expiration (Date or ISO‐string)
                Date expiration = null;
                Object expirationObj = details.get("expiration");
                if (expirationObj instanceof Date) {
                    expiration = (Date) expirationObj;
                } else if (expirationObj instanceof String) {
                    try {
                        LocalDate localDate = LocalDate.parse(
                            (String) expirationObj,
                            DateTimeFormatter.ISO_LOCAL_DATE
                        );
                        expiration = Date.from(
                            localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                        );
                    } catch (Exception e) {
                    	log.error("Invalid expiration format: " + expirationObj);
                    }
                }

                if (expiration != null 
                    && expiration.after(now) 
                    && expiration.before(oneWeekFromNow)) {
                    ObjectBoundary boundary = this.objectConverter.toBoundary(currentObject);
                    requiredProducts.add(boundary);
                }
            }
        }

        return requiredProducts;
    }


}
