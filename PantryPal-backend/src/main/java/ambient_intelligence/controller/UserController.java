package ambient_intelligence.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import ambient_intelligence.boundary.NewUserBoundary;
import ambient_intelligence.boundary.UserBoundary;
import ambient_intelligence.converters.UserConverter;
import ambient_intelligence.logic.UsersService;
import ambient_intelligence.service.ObjectServiceImpl;
import ambient_intelligence.service.PantryUnauthorizedException;

@RestController
@RequestMapping("/ambient-intelligence/users")
public class UserController {
    private final UsersService usersService;
	private Log log = LogFactory.getLog(ObjectServiceImpl.class);
	private UserConverter userConverter;

    public UserController(UsersService usersService, UserConverter userConverter) {
        this.usersService = usersService;
        this.userConverter = userConverter;
    }

    @PostMapping(
    		consumes = { MediaType.APPLICATION_JSON_VALUE }, 
    		produces = { MediaType.APPLICATION_JSON_VALUE })
    @CrossOrigin(origins = "*")
    public UserBoundary createUser(@RequestBody NewUserBoundary newUser) {
    	log.info("createUser(" + newUser.toString() + ")");
    	return usersService.createUser(this.userConverter.newUsertoUserBoundary(newUser));
    }

    @GetMapping(
    		path = {"/login/{systemID}/{userEmail}"},
    		produces = { MediaType.APPLICATION_JSON_VALUE })
    @CrossOrigin(origins = "*")
    public UserBoundary login(
    		@PathVariable("systemID") String systemID,
    		@PathVariable("userEmail") String email) {
        return usersService.login(systemID, email)
                .orElseThrow(() -> new PantryUnauthorizedException("Could not found user: " + email + " " + systemID));
    }

    @PutMapping(
    		path = {"/{systemID}/{email}"},
    		consumes = { MediaType.APPLICATION_JSON_VALUE })
    @CrossOrigin(origins = "*")
    public void updateUser(
    		@PathVariable("systemID") String systemID,
            @PathVariable("email") String email,
            @RequestBody UserBoundary update) {
    	usersService.updateUser(systemID, email, update);
    }
}

