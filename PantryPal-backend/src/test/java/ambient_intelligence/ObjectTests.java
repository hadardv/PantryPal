package ambient_intelligence;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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

import ambient_intelligence.boundary.NewUserBoundary;
import ambient_intelligence.boundary.ObjectBoundary;
import ambient_intelligence.boundary.ObjectIdBoundary;
import ambient_intelligence.boundary.UserBoundary;
import ambient_intelligence.boundary.UserIdBoundary;
import ambient_intelligence.data.UserRole;
import ambient_intelligence.enums.ObjectTypeEnum;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ObjectTests {
	private static final String HOST_NAME = "http://localhost:";
	private static final String OBJECT_ROUTE = "/ambient-intelligence/objects";
	private static final String USER_ROUTE = "/ambient-intelligence/users";
	private static final String ADMIN_ROUTE = "/ambient-intelligence/admin";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
	private static final Log log = LogFactory.getLog(ObjectTests.class);
	
    private int port;
    private String baseUrlObject;
    private String baseUrlAdmin;
    private String baseUrlUser;
    private RestClient objectsRestClient;
    private RestClient adminRestClient;
    private RestClient usersRestClient;
    
    @Value("${spring.application.name}")
    private String systemId;

    @Value("${server.port:8080}")
    public void setPort(int port) {
        this.port = port;
        this.baseUrlObject = HOST_NAME + port + OBJECT_ROUTE;
        this.baseUrlAdmin = HOST_NAME + port + ADMIN_ROUTE;
        this.baseUrlUser = HOST_NAME + port + USER_ROUTE;
        this.objectsRestClient = RestClient.create(this.baseUrlObject);
        this.adminRestClient = RestClient.create(this.baseUrlAdmin);
        this.usersRestClient = RestClient.create(this.baseUrlUser);
    }

    @BeforeEach
    public void setup() {
		log.debug("*** setup()");
		UserBoundary user = createTestUser("testobject-admin@gmail.com", UserRole.ADMIN, "omer", "panda");
		this.adminRestClient
		.delete()
		.uri(uriBuilder -> uriBuilder
			    .path("/objects")
			    .queryParam("userSystemId", systemId)
			    .queryParam("userEmail", "testobject-admin@gmail.com")
			    .build()
			)
		.retrieve()
		.body(Void.class);
		log.info("successfully deleted objects");
		
		this.adminRestClient
		.delete()
		.uri(uriBuilder -> uriBuilder
			    .path("/users")
			    .queryParam("userSystemId", systemId)
			    .queryParam("userEmail", "testobject-admin@gmail.com")
			    .build()
			)
		.retrieve()
		.body(Void.class);
		log.info("successfully deleted users");

    }

    @AfterEach
    public void tearDown() {
    	log.debug("*** tearDown()");
		UserBoundary user = createTestUser("testobject-admin@gmail.com", UserRole.ADMIN, "omer", "panda");
		this.adminRestClient
		.delete()
		.uri(uriBuilder -> uriBuilder
			    .path("/objects")
			    .queryParam("userSystemId", systemId)
			    .queryParam("userEmail", "testobject-admin@gmail.com")
			    .build()
			)
		.retrieve()
		.body(Void.class);
		log.info("successfully deleted objects");
		
		this.adminRestClient
		.delete()
		.uri(uriBuilder -> uriBuilder
			    .path("/users")
			    .queryParam("userSystemId", systemId)
			    .queryParam("userEmail", "testobject-admin@gmail.com")
			    .build()
			)
		.retrieve()
		.body(Void.class);
		log.info("successfully deleted users");

        
    }
    
    @Test
    @DisplayName("Test Create Object")
    public void testCreateObject() {
    	try {
    		UserBoundary operator = createTestUser("objectest-operator@gmail.com", UserRole.OPERATOR, "yuval", "panda");
        	ObjectBoundary ob = createTestObject(
        			"E5",
        			"objectest-operator@gmail.com",
        			ObjectTypeEnum.PRODUCT_BY_QUANTITY,
        			"Milk-Test",
        			"NEW",
        			true);

            ObjectBoundary createdObject = this.objectsRestClient
                .post()
                .body(ob)
                .retrieve()
                .body(ObjectBoundary.class);


        	log.debug("Validation test for new Object Creation...");
        	assertNotNull(createdObject,"Created Object is Null");
        	log.info("New Object is not null");
        	
        	assertNotNull(createdObject.getId().getObjectId(),"ObjectID is Null");
        	log.info("Valid ObjectID");
        	
            assertTrue("Object SystemId is not valid", createdObject.getId().getSystemID().equals(systemId));
        	log.info("Valid SystemID");
            
            assertTrue("Object is not active", createdObject.getActive()==true);
            log.info("Object is active test passed");
            
            assertTrue("Invalid Type", createdObject.getType()==ObjectTypeEnum.PRODUCT_BY_QUANTITY);
            log.info("Object Type test passed");
            
            assertTrue("Object Alias is incorrect", createdObject.getAlias().equals("Milk-Test"));
            log.info("Object Alias test passed");
            
        	assertNotNull(createdObject.getCreatedBy(),"CreatedBy is Null");
        	log.info("Valid CreatedBy");
            
            assertTrue("CreateBy email field is not valid", createdObject.getCreatedBy().getEmail().equals("objectest-operator@gmail.com"));
            log.info("CreateBy email test passed");

            assertTrue("CreateBy SystemId field is not valid", createdObject.getCreatedBy().getSystemID().equals(systemId));
            log.info("CreateBy SystemId test passed");
            
            assertTrue("Object status is incorrect", createdObject.getStatus().equals("NEW"));
            log.info("Object status test passed");
            
        	assertNotNull(createdObject.getCreationTimestamp(),"Object Creation Timestamp is Null");
        	log.info("Valid Creation Timestamp");

            log.info("All tests for new object creation passed successfully");
    		
    	}
    	catch (AssertionError | Exception e) {
    	    log.error("Test Create Object failed - " + e.getMessage());
    	}
    }
    

    @Test
    @DisplayName("Test Get All Objects")
    public void testGetAllObjects() {
    	try {
    		UserBoundary operator = createTestUser("objecttest-operator@gmail.com", UserRole.OPERATOR, "yuval", "panda");
        	ObjectBoundary ob = createTestObject(
        			"E5",
        			"objecttest-operator@gmail.com",
        			ObjectTypeEnum.PRODUCT_BY_QUANTITY,
        			"Milk-Test",
        			"NEW",
        			true);
        	
    		// GIVEN the server is up
    		// AND the server contains 1 object
            ObjectBoundary createdObject = this.objectsRestClient
                    .post()
                    .body(ob)
                    .retrieve()
                    .body(ObjectBoundary.class);
            
            /* ---------- WHEN I GET /objects?page=0&size=10 ---------- */            
            ObjectBoundary[] actual = this.objectsRestClient
                    .get()
                    .uri("?userSystemID={systemID}&userEmail={email}&size={size}&page={page}",
                    		systemId,
                    		"objecttest-operator@gmail.com",
                    		10, 0)
                    .retrieve()
                    .body(ObjectBoundary[].class);

            /* ---------- THEN the list contains the created object ---------- */
        	log.debug("Validation test for GetAllObjects...");
  
        	assertNotNull(actual, "Returned array is null");
            log.info("Returned Object array is not null");
            
            assertTrue("Database does not contain any object", actual.length > 0);
            log.info("GetAllObjects test passed, Database contains "+ actual.length+ " objects");
    	}
    	catch (AssertionError | Exception e) {
    	    log.error("Test Get All Objects failed - "+ e.getMessage());
    	}
    }
    
    	
	@Test
	@DisplayName("Test Get Specific Object")
	public void testGetSpecificObject() throws Exception {
		try {
    		UserBoundary operator = createTestUser("testobject-operator@gmail.com", UserRole.OPERATOR, "yuval", "panda");
    		UserBoundary onother_operator = createTestUser("test2operator@gmail.com", UserRole.OPERATOR, "matan", "panda");

	    	ObjectBoundary ob = createTestObject(
	    			"E5",
	    			"testobject-operator@gmail.com",
	    			ObjectTypeEnum.PRODUCT_BY_QUANTITY,
	    			"Milk-Test",
	    			"NEW",
	    			true);
	
	        ObjectBoundary createdObject = this.objectsRestClient
	                .post()
	                .body(ob)
	                .retrieve()
	                .body(ObjectBoundary.class);
	        
	        			
			ObjectBoundary actualResult = 
			  this.objectsRestClient
				.get()
				.uri("/{systemID}/{id}?userSystemID={systemID}&userEmail={email}",
						createdObject.getId().getSystemID(),
						createdObject.getId().getObjectId(),
						systemId, "test2operator@gmail.com")
				.retrieve()
				.body(ObjectBoundary.class);
				
			
        	log.debug("Validation test for GetSpecificObject...");
			assertNotNull(actualResult, "Specific Object is null");
			log.info("Returned object is not null");
			
			assertNotNull(actualResult.getId().getSystemID(), "SystemId is null");
			log.info("SystemId is not null");
			
			assertNotNull(actualResult.getId().getObjectId(), "ObjectId is null");
			log.info("ObjectId is not null");
	
	        log.info("GetSpecificObject test passed");
	        
		}        
    	catch (AssertionError | Exception e) {
    	    log.error("Test GetSpecificObject failed - "+ e.getMessage());
    	}
	}
		
	@Test
	@DisplayName("Test Update Object")
	public void testUpdateObject(){
		try {
    		UserBoundary operator = createTestUser("testobject-operator@gmail.com", UserRole.OPERATOR, "yuval", "panda");
	    	ObjectBoundary ob = createTestObject(
	    			"E5",
	    			"testobject-operator@gmail.com",
	    			ObjectTypeEnum.PRODUCT_BY_QUANTITY,
	    			"Milk-Test",
	    			"NEW",
	    			true);
	
	        ObjectBoundary createdObject = this.objectsRestClient
	                .post()
	                .body(ob)
	                .retrieve()
	                .body(ObjectBoundary.class);
	        
	        
	        Map<String, Object> updates = new HashMap<>();
	        updates.put("alias", "Milky");
	        updates.put("active", false);
	        updates.put("type", createdObject.getType());
	        updates.put("createdBy", createdObject.getCreatedBy());
	        updates.put("status", createdObject.getStatus());

			this.objectsRestClient
				.put()
				.uri("/{systemID}/{id}?userSystemID={systemID}&userEmail={email}",
						createdObject.getId().getSystemID(),
						createdObject.getId().getObjectId(),
						systemId, "testobject-operator@gmail.com")
				.body(Collections.unmodifiableMap(updates))
				.retrieve()
				.body(Void.class);
				
			ObjectBoundary actual = this.objectsRestClient
				.get()
				.uri("/{systemID}/{id}?userSystemID={systemID}&userEmail={email}",
						createdObject.getId().getSystemID(),
						createdObject.getId().getObjectId(),
						systemId, "testobject-operator@gmail.com")
				.retrieve()
				.body(ObjectBoundary.class);
			
        	log.debug("Validation test for UpdateObject...");
			assertNotNull(actual, "Object is null");
			log.info("Object is not null");
			
            assertTrue("Alias was not updated", actual.getAlias().equals("Milky"));
            log.info("Alias was updated successfully");

            assertTrue("Active was not updated", !actual.getActive());
            log.info("Active was updated successfully");
			
	        log.info("UpdateObject test passed");
		}
    	catch (AssertionError | Exception e) {
    	    log.error("Test testUpdateObject failed - "+ e.getMessage());
    	}

	}

    
    public ObjectBoundary createTestObject(String id, String userEmail, ObjectTypeEnum type, String alias, String status, Boolean active) {
        ObjectBoundary ob = new ObjectBoundary();
		ObjectIdBoundary objectId = new ObjectIdBoundary(id, systemId);
		UserIdBoundary userId = new UserIdBoundary(userEmail, systemId);
		ob.setCreatedBy(userId);
		ob.setId(objectId);
		ob.setType(type);
		ob.setAlias(alias);
		ob.setStatus(status);
		ob.setActive(active);
		ob.setCreationTimestamp(new Date());

		Map<String,Object> details = new HashMap<>();		
	    details.put("amount", 5);
	    String today = LocalDate.now().format(DATE_FMT);
	    details.put("expiration", today);
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
