package ambient_intelligence;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
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

import ambient_intelligence.boundary.NewUserBoundary;
import ambient_intelligence.boundary.UserBoundary;
import ambient_intelligence.data.UserRole;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class UserTests {
	private static final String HOST_NAME = "http://localhost:";
	private static final String USER_ROUTE = "/ambient-intelligence/users";
	private static final String ADMIN_ROUTE = "/ambient-intelligence/admin";
	private static final Log log = LogFactory.getLog(UserTests.class);
	
    private int port;
    private String baseUrlUsers;
    private String baseUrlAdmin;
    private RestClient usersRestClient;
    private RestClient adminRestClient;
    
    @Value("${spring.application.name}")
    private String systemId;

    @Value("${server.port:8080}")
    public void setPort(int port) {
        this.port = port;
        this.baseUrlUsers = HOST_NAME + port + USER_ROUTE;
        this.baseUrlAdmin = HOST_NAME + port + ADMIN_ROUTE;
        this.usersRestClient = RestClient.create(this.baseUrlUsers);
        this.adminRestClient = RestClient.create(this.baseUrlAdmin);
    }

    @BeforeEach
    public void setup() {
        log.debug("*** setup()");
		NewUserBoundary newUser = createNewUser("usersetup-admin@gmail.com", UserRole.ADMIN, "omer", "panda");
		
        this.usersRestClient
                .post()
                .body(newUser)
                .retrieve()
                .body(UserBoundary.class);

		this.adminRestClient
		.delete()
		.uri(uriBuilder -> uriBuilder
			    .path("/users")
			    .queryParam("userSystemId", systemId)
			    .queryParam("userEmail", "usersetup-admin@gmail.com")
			    .build()
			)
		.retrieve()
		.body(Void.class);
		log.info("successfully deleted users");


    }

    @AfterEach
    public void tearDown() {
    	log.debug("*** tearDown()");
		NewUserBoundary newUser = createNewUser("usersetup-admin@gmail.com", UserRole.ADMIN, "omer", "panda");
		
        this.usersRestClient
                .post()
                .body(newUser)
                .retrieve()
                .body(UserBoundary.class);

		this.adminRestClient
		.delete()
		.uri(uriBuilder -> uriBuilder
			    .path("/users")
			    .queryParam("userSystemId", systemId)
			    .queryParam("userEmail", "usersetup-admin@gmail.com")
			    .build()
			)
		.retrieve()
		.body(Void.class);
		log.info("successfully deleted users");

    }
    
    @Test
    @DisplayName("Test Create User")
    public void testCreateUser() {
    	try {
        	NewUserBoundary user = createNewUser(
        			"usertest-user@gmail.com", UserRole.END_USER, "omer", "panda");

            UserBoundary createdUser = this.usersRestClient
                .post()
                .body(user)
                .retrieve()
                .body(UserBoundary.class);


        	log.debug("Validation test for CreateUser...");
        	assertNotNull(createdUser.getUserId(), "new user id is null");
        	log.info("objectid is not null");
        	assertTrue("SystemId is not valid", createdUser.getUserId().getSystemID().equals(systemId));
        	log.info("SystemId is valid");
        	assertTrue("User Email is not valid", createdUser.getUserId().getEmail().equals("usertest-user@gmail.com"));
        	log.info("User Email is valid");        	
        	assertTrue("Role is not valid", createdUser.getRole()==UserRole.END_USER);
        	log.info("Role is correct");
        	assertTrue("Username is not valid", createdUser.getUserName().equals("omer"));
        	log.info("Username is correct");
        	assertTrue("Avatar is not valid", createdUser.getAvatar().equals("panda"));
        	log.info("Avatar is correct");

            log.info("All tests for new user creation passed successfully");
    		
    	}
    	catch (AssertionError | Exception e) {
    	    log.error("Test Create User failed - " + e.getMessage());
    	}
    }
    
	@Test
	@DisplayName("Test Get Specific Object")
	public void testGetSpecificUser() throws Exception {
		try {		
        	NewUserBoundary newUser = createNewUser(
        			"usertest-operator@gmail.com", UserRole.OPERATOR, "noa", "lion");

            this.usersRestClient
                .post()
                .body(newUser)
                .retrieve()
                .body(UserBoundary.class);
	        
	        			
            UserBoundary actualResult = 
			  this.usersRestClient
				.get()
				.uri("/login/{systemID}/{userEmail}",
						systemId,
						"usertest-operator@gmail.com")
				.retrieve()
				.body(UserBoundary.class);
				
			
        	log.debug("Validation test for GetSpecificUser...");
			assertNotNull(actualResult, "User is null");
			log.info("Returned user is not null");
        	assertTrue("Returned user with not matching email address as request", actualResult.getUserId().getEmail().equals("usertest-operator@gmail.com"));
        	log.info("Returned email user is valid");
        	
	        log.info("GetSpecificUser test passed");
	        
		}        
    	catch (AssertionError | Exception e) {
    	    log.error("Test GetSpecificUser failed - "+ e.getMessage());
    	}

	}
	
	
	@Test
	@DisplayName("Test Update User")
	public void testUpdateUser(){
		try {
			// GIVEN the server is up
			// AND the database contains an object
        	NewUserBoundary newUser = createNewUser(
        			"usertest-admin@gmail.com", UserRole.ADMIN, "noa", "lion");

            UserBoundary user = this.usersRestClient
                .post()
                .body(newUser)
                .retrieve()
                .body(UserBoundary.class);
	        
	        Map<String, Object> updates = new HashMap<>();
	        updates.put("role", UserRole.OPERATOR);
	        updates.put("userName", "noa-updated");
	        updates.put("avatar", "dog");
	        updates.put("userId", user.getUserId());
				 
			this.usersRestClient
				.put()
				.uri("/{systemID}/{email}",
						systemId,
						"usertest-admin@gmail.com")
				.body(Collections.unmodifiableMap(updates))
				.retrieve()
				.body(Void.class);
				
            UserBoundary actualResult = 
			  this.usersRestClient
				.get()
				.uri("/login/{systemID}/{userEmail}",
						systemId,
						"usertest-admin@gmail.com")
				.retrieve()
				.body(UserBoundary.class);
			
        	log.debug("Validation test for UpdateUser...");
			assertNotNull(actualResult, "User is null");
			log.info("User is not null");
			
            assertTrue("UserId email fields is not valid", actualResult.getUserId().getEmail().equals("usertest-admin@gmail.com"));
            log.info("UserId email field is correct");
            
            assertTrue("UserId systemId field is not valid", actualResult.getUserId().getSystemID().equals(systemId));
            log.info("UserId systemId field is correct");

            assertTrue("Avatar Update Failed", actualResult.getAvatar().equals("dog"));
            log.info("Avatar was updated successfully");
            
            assertTrue("UserName Update Failed", actualResult.getUserName().equals("noa-updated"));
            log.info("UserName was updated successfully");

            assertTrue("Role Update Failed", actualResult.getRole()==UserRole.OPERATOR);
            log.info("Role was updated successfully");
			
	        log.info("UpdateUser test passed");
		}
    	catch (AssertionError | Exception e) {
    	    log.error("Test testUpdateUser failed - "+ e.getMessage());
    	}

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
    
    
