package ambient_intelligence.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import ambient_intelligence.boundary.NewUserBoundary;
import ambient_intelligence.boundary.ObjectBoundary;
import ambient_intelligence.boundary.ObjectIdBoundary;
import ambient_intelligence.boundary.UserBoundary;
import ambient_intelligence.boundary.UserIdBoundary;
import ambient_intelligence.converters.UserConverter;
import ambient_intelligence.data.UserRole;
import ambient_intelligence.enums.ObjectTypeEnum;
import ambient_intelligence.logic.ObjectService;
import ambient_intelligence.logic.UsersService;
import ambient_intelligence.service.ObjectServiceImpl;

@Component
@Profile("ManualGeneration")
public class DataInitializer implements CommandLineRunner {
	private Log log = LogFactory.getLog(ObjectServiceImpl.class);
    private final UsersService usersService;
    private final ObjectService objectService;
    private UserConverter userConverter;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;


    @Value("${spring.application.name}")
    private String systemId;

    public DataInitializer(UsersService usersService,
                           ObjectService objectService, UserConverter userConverter) {
        this.usersService = usersService;
        this.objectService = objectService;
        this.userConverter = userConverter;
    }
    

    @Override
    public void run(String... args) throws Exception {
        // 1) Clean slate: only ADMIN can delete, so we create admin first
        NewUserBoundary adminCleaner = new NewUserBoundary(
                "admn@example.com",
                UserRole.ADMIN,
                "Administrator",
                "avatar-admin.png");
        UserBoundary admina = this.usersService.createUser(this.userConverter.newUsertoUserBoundary(adminCleaner));

        // delete all existing data
        this.objectService.deleteAllObjects(systemId, admina.getUserId().getEmail());
        this.usersService.deleteAllUsers(systemId, admina.getUserId().getEmail());
        
        // 1) Clean slate: only ADMIN can delete, so we create admin first
        NewUserBoundary adminInit = new NewUserBoundary(
                "admin@example.com",
                UserRole.ADMIN,
                "Administrator",
                "avatarAdmin");
        UserBoundary admin = this.usersService.createUser(this.userConverter.newUsertoUserBoundary(adminInit));
        log.info("Created Admin User: " + admin);

        // 2) Create sample users
        NewUserBoundary operatorInit = new NewUserBoundary(
                "operator@example.com",
                UserRole.OPERATOR,
                "Operator",
                "avatarOperator");
        UserBoundary operator = this.usersService.createUser(this.userConverter.newUsertoUserBoundary(operatorInit));
        log.info("Created Operator User: " + operator);

        NewUserBoundary endUserInit = new NewUserBoundary(
                "user@example.com",
                UserRole.END_USER,
                "EndUser",
                "avatarUser");
        UserBoundary endUser = this.usersService.createUser(this.userConverter.newUsertoUserBoundary(endUserInit));
        log.info("Created End_User User: " + endUser);

        // 3) Create sample objects
        ObjectBoundary objA = buildObject("A1", ObjectTypeEnum.PRODUCT_BY_QUANTITY, "Milk", operator, true);
        ObjectBoundary createdA = this.objectService.create(objA);
        log.info("Created object: " + createdA);

        ObjectBoundary objB = buildObject("B2", ObjectTypeEnum.PRODUCT_BY_WEIGHT, "Rice", operator, false);
        ObjectBoundary createdB = this.objectService.create(objB);
        log.info("Created object: " + createdB);
        
        ObjectBoundary objC = buildObject("C3", ObjectTypeEnum.SHOPPING_LIST, "ShoppingList1", operator, true);
        ObjectBoundary createdC = this.objectService.create(objC);
        log.info("Created object: " + createdC);

        ObjectBoundary objD = buildObject("D4", ObjectTypeEnum.INVENTORY, "Inventory1", operator, true);
        ObjectBoundary createdD = this.objectService.create(objD);
        log.info("Created object: " + createdD);
        
        // 4) Bind two objects (A1 and B2) as children of D4 using ADMIN credentials
        this.objectService.bindObjects(
                systemId, createdD.getId().getObjectId(),
                systemId, createdA.getId().getObjectId(),
                operator.getUserId().getSystemID(), operator.getUserId().getEmail());

        this.objectService.bindObjects(
                systemId, createdD.getId().getObjectId(),
                systemId, createdB.getId().getObjectId(),
                operator.getUserId().getSystemID(), operator.getUserId().getEmail());

        log.info("Data initialization complete.");

    }
    
    private ObjectBoundary buildObject(
    		String localId,
            ObjectTypeEnum type,
            String alias,
            UserBoundary creator,
            boolean active
            ) {
		ObjectBoundary ob = new ObjectBoundary();
		
		// ObjectIdBoundary will be assigned by service if omitted, but we set local for clarity
		ObjectIdBoundary oid = new ObjectIdBoundary(localId, systemId);
		ob.setId(oid);
		
		ob.setType(type);
		ob.setAlias(alias);
		ob.setStatus("NEW");
		ob.setActive(active);
		ob.setCreationTimestamp(new Date());
		
		// link to creating user
		UserIdBoundary ub = new UserIdBoundary(
		creator.getUserId().getEmail(), systemId);
		ob.setCreatedBy(ub);
		
		// add sample details
		Map<String,Object> details = new HashMap<>();
		if (type.equals(ObjectTypeEnum.PRODUCT_BY_QUANTITY)
				 || type.equals(ObjectTypeEnum.PRODUCT_BY_WEIGHT)) {
				    details.put("amount", 5);
				    String today = LocalDate.now().format(DATE_FMT);
				    details.put("expiration", today);
				}

		        
		ob.setObjectDetails(details);
		
		return ob;
    }
}

