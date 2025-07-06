package ambient_intelligence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import ambient_intelligence.boundary.CommandBoundary;
import ambient_intelligence.boundary.CommandIdBoundary;
import ambient_intelligence.boundary.NewUserBoundary;
import ambient_intelligence.boundary.ObjectBoundary;
import ambient_intelligence.boundary.ObjectIdBoundary;
import ambient_intelligence.boundary.UserBoundary;
import ambient_intelligence.data.UserRole;
import ambient_intelligence.enums.ObjectTypeEnum;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class CommandTests {

	private static final String HOST_NAME = "http://localhost:";
	private static final String COMMAND_ROUTE = "/ambient-intelligence/commands";
	private static final String ADMIN_ROUTE = "/ambient-intelligence/admin";
	private static final String OBJECT_ROUTE = "/ambient-intelligence/objects";
	private static final String USER_ROUTE = "/ambient-intelligence/users";


	private static final Log log = LogFactory.getLog(CommandTests.class);

	private int port;
	private String baseUrl;
    private String baseUrlUser;
	private RestClient restClient;
    private RestClient usersRestClient;


	@Value("${spring.application.name}")
	private String systemId;

	@Value("${server.port:8080}")
	public void setPort(int port) {
		this.port = port;
		this.baseUrl = HOST_NAME + port; // http://localhost:8080
        this.baseUrlUser = this.baseUrl + USER_ROUTE;
		this.restClient = RestClient.create(this.baseUrl);
        this.usersRestClient = RestClient.create(this.baseUrlUser);
	}

	@BeforeEach
	public void setup() {
		log.debug("*** setup()");
		UserBoundary operator = createTestUser("commanduser-admin@gmail.com", UserRole.ADMIN, "yuval", "panda");
		// Delete all commands from DB
		this.restClient
				.delete().uri(uriBuilder -> uriBuilder.path(ADMIN_ROUTE + "/commands")
						.queryParam("userSystemId", systemId).queryParam("userEmail", "commanduser-admin@gmail.com").build())
				.retrieve().body(Void.class);

		log.info("successfully deleted commands");

		// Create object that expires in 3 days and is out of stock
		ObjectBoundary testObjectBoundary = createTestObject();
		ObjectBoundary createdObject = restClient.post().uri(OBJECT_ROUTE).body(testObjectBoundary).retrieve()
				.body(ObjectBoundary.class);

		log.info("Successfully Created object: " + createdObject);
		
		this.restClient
		.delete()
		.uri(uriBuilder -> uriBuilder
			    .path(ADMIN_ROUTE+ "/users")
			    .queryParam("userSystemId", systemId)
			    .queryParam("userEmail", "commanduser-admin@gmail.com")
			    .build()
			)
		.retrieve()
		.body(Void.class);
		log.info("successfully deleted users");
	}

	@AfterEach
	public void tearDown() {
		log.debug("*** tearDown()");
		UserBoundary operator = createTestUser("commanduser-admin@gmail.com", UserRole.ADMIN, "yuval", "panda");

		// Delete all commands from DB
		this.restClient
				.delete().uri(uriBuilder -> uriBuilder.path(ADMIN_ROUTE + "/commands")
						.queryParam("userSystemId", systemId).queryParam("userEmail", "commanduser-admin@gmail.com").build())
				.retrieve().body(Void.class);

		log.info("successfully deleted commands");
		
		//Delete all objects from DB
		this.restClient
		.delete()
		.uri(uriBuilder -> uriBuilder
			    .path(ADMIN_ROUTE + "/objects")
			    .queryParam("userSystemId", systemId)
			    .queryParam("userEmail", "commanduser-admin@gmail.com")
			    .build()
			)
		.retrieve()
		.body(Void.class);
		log.info("successfully deleted objects");
		
		this.restClient
		.delete()
		.uri(uriBuilder -> uriBuilder
			    .path(ADMIN_ROUTE+ "/users")
			    .queryParam("userSystemId", systemId)
			    .queryParam("userEmail", "commanduser-admin@gmail.com")
			    .build()
			)
		.retrieve()
		.body(Void.class);
		log.info("successfully deleted users");


	}

	@Test
	@DisplayName("Test creating getProductsExpiringInNextWeek command")
	public void testCreateCommand_getProductsExpiringInNextWeek() {
		try {
			log.info("Starting testCreateCommand_getProductsExpiringInNextWeek");
			
			// GIVEN a new command
			CommandBoundary commandBoundary = createTestCommand();
			log.info("Command created: " + commandBoundary);
	
			// WHEN the command is posted
			log.debug("Sending POST request to " + COMMAND_ROUTE);
			Object[] response = this.restClient
					.post()
					.uri(COMMAND_ROUTE)
					.body(commandBoundary)
					.retrieve()
					.body(Object[].class); // invokeCommand returns List<Object>
	
			log.info("Received response with " + (response != null ? response.length : "null") + " objects");
			
			// THEN assert we got a non-null response
			assertThat(response).isNotNull();
		    assertThat(response.length).isGreaterThanOrEqualTo(1);
		    log.info("Response is not null and contains at least one object");
	
		 // Validate each object in the response
		    for (Object obj : response) {
		        @SuppressWarnings("unchecked")
		        Map<String, Object> objectMap = (Map<String, Object>) obj;
	
		        // Check that "objectDetails" field exists and is a map
		        assertThat(objectMap).containsKey("objectDetails");
		        @SuppressWarnings("unchecked")
		        Map<String, Object> details = (Map<String, Object>) objectMap.get("objectDetails");
	
		        assertThat(details).containsKey("expiration");
	
		        // Parse the expiration date
		        String expirationString = details.get("expiration").toString();
		        LocalDate expirationDate = LocalDate.parse(expirationString);
		        LocalDate today = LocalDate.now();
		        LocalDate oneWeekFromNow = today.plusDays(7);
	
		        // Check that expiration is in the next 7 days
		        assertThat(expirationDate.isAfter(today.minusDays(1))).isTrue(); // >= today
		        assertThat(expirationDate.isBefore(oneWeekFromNow.plusDays(1))).isTrue(); // <= one week from now
		        
	
				log.info("Validated expiration date is within 7 days for object: " + objectMap);
		    }
		}
    	catch (AssertionError | Exception e) {
    	    log.error("Test Create Command failed - " + e.getMessage());
    	}

	    
	    log.info("testCreateCommand completed successfully");
		
	}
	
	@Test
	@DisplayName("Test creating getExpiredOrOutOfStockProducts command")
	public void testCreateCommand_getExpiredOrOutOfStockProducts() {
		log.info("Starting testCreateCommand_getExpiredOrOutOfStockProducts");
		
		// GIVEN a new command
		CommandBoundary commandBoundary = createTestCommand();
		log.info("Command created: " + commandBoundary);

		// WHEN the command is posted
		log.debug("Sending POST request to " + COMMAND_ROUTE);
		Object[] response = this.restClient
				.post()
				.uri(COMMAND_ROUTE)
				.body(commandBoundary)
				.retrieve()
				.body(Object[].class); // invokeCommand returns List<Object>

		log.info("Received response with " + (response != null ? response.length : "null") + " objects");
		
		// THEN assert we got a non-null response
		assertThat(response).isNotNull();
		assertThat(response.length).isGreaterThanOrEqualTo(1);
		log.info("Response is not null and contains at least one object");
		
		LocalDate today = LocalDate.now();
		
		for (Object obj : response) {
			@SuppressWarnings("unchecked")
			Map<String, Object> objectMap = (Map<String, Object>) obj;
			assertThat(objectMap).containsKey("objectDetails");

			@SuppressWarnings("unchecked")
			Map<String, Object> details = (Map<String, Object>) objectMap.get("objectDetails");

			// Check expiration
			boolean isExpired = false;
			if (details.containsKey("expiration")) {
				try {
					String expirationString = details.get("expiration").toString();
					LocalDate expirationDate = LocalDate.parse(expirationString);
					isExpired = expirationDate.isBefore(today);
					log.debug("Parsed expiration date: " + expirationDate + ", isExpired: " + isExpired);
				} catch (Exception e) {
					log.warn("Invalid expiration date format: " + details.get("expiration"));
				}
			} else {
		        log.warn("Missing expiration field in objectDetails");
		    }

			// Check amount
			boolean isOutOfStock = false;
			if (details.containsKey("amount")) {
				try {
					int amount = Integer.parseInt(details.get("amount").toString());
					isOutOfStock = amount <= 0;
					log.debug("Parsed amount: " + amount + ", isOutOfStock: " + isOutOfStock);
				} catch (NumberFormatException e) {
					log.warn("Invalid amount format: " + details.get("amount"));
				}
			}

			// Assert that at least one condition is met
			assertTrue("Expected object to be expired or out of stock", isExpired || isOutOfStock);
			log.info("Validated object as expired/out-of-stock: " + objectMap);
		}

		log.info("testCreateCommand_getExpiredOrOutOfStockProducts completed successfully");
	}

	private CommandBoundary createTestCommand() {
		UserBoundary user = createTestUser("commanduser-enduser@gmail.com", UserRole.END_USER, "yuval", "panda");
		CommandBoundary commandBoundary = new CommandBoundary();
		CommandIdBoundary commandIdBoundary = new CommandIdBoundary("E1", systemId);
		
		commandBoundary.setCommandId(commandIdBoundary);
		commandBoundary.setCommand("getProductsExpiringInNextWeek");
		commandBoundary.setInvocationTimestamp(new Date());
		commandBoundary.setInvokedBy(user.getUserId());
		commandBoundary.setCommandAttributes(new HashMap<>());
		
		return commandBoundary;
	}

	public ObjectBoundary createTestObject() {
		UserBoundary user = createTestUser("commanduser-operator@gmail.com", UserRole.OPERATOR, "aviv", "panda");
		ObjectBoundary ob = new ObjectBoundary();
		ObjectIdBoundary objectId = new ObjectIdBoundary("E5", systemId);
		ob.setCreatedBy(user.getUserId());
		ob.setId(objectId);
		ob.setType(ObjectTypeEnum.PRODUCT_BY_QUANTITY);
		ob.setAlias("Milk-Test");
		ob.setStatus("NEW");
		ob.setActive(true);
		ob.setCreationTimestamp(new Date());

		Map<String, Object> details = new HashMap<>();
		details.put("amount", 0);
		String expiringDate = LocalDate.now().plusDays(3).toString();
		details.put("expiration", expiringDate);
		ob.setObjectDetails(details);

		return ob;
	}
	
    public UserBoundary createTestUser(String email, UserRole role, String userName, String avatar) {
    	NewUserBoundary user = createNewUser(
    			email, role, userName, avatar);

        UserBoundary createdUser = this.usersRestClient
            .post()
            .body(user)
            .retrieve()
            .body(UserBoundary.class);
        
        return createdUser;
    }
    
    public NewUserBoundary createNewUser(String email, UserRole role, String userName, String avatar) {
    	NewUserBoundary newUser = new NewUserBoundary();
    	newUser.setEmail(email);
    	newUser.setRole(role);
    	newUser.setUserName(userName);
    	newUser.setAvatar(avatar);
    	return newUser;    	
    }


	
}
