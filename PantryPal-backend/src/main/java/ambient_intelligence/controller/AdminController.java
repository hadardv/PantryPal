package ambient_intelligence.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ambient_intelligence.boundary.CommandBoundary;
import ambient_intelligence.boundary.UserBoundary;
import ambient_intelligence.logic.CommandService;
import ambient_intelligence.logic.ObjectService;
import ambient_intelligence.logic.UsersService;


@RestController
@RequestMapping("/ambient-intelligence/admin")
public class AdminController {
	
	private final UsersService usersService; 
	private final ObjectService objectService;
	private final CommandService commandService;
	
	public AdminController(UsersService usersService, ObjectService objectService, CommandService commandService) {
        this.usersService = usersService;
        this.objectService = objectService;
        this.commandService = commandService;
    }
	

	@DeleteMapping("/users")
	@CrossOrigin(origins = "*")
	public void deleteAllUsers(
			@RequestParam(name = "userSystemId", required = true) String systemId,
			@RequestParam(name = "userEmail", required = true) String email
			) {
		usersService.deleteAllUsers(systemId, email);
	}
	
	@DeleteMapping("/objects")
	@CrossOrigin(origins = "*")
	public void deleteAllObjects(
			@RequestParam(name = "userSystemId", required = true) String systemId,
			@RequestParam(name = "userEmail", required = true) String email
			) {
		objectService.deleteAllObjects(systemId,email);
	}
	
	@DeleteMapping("/commands")
	@CrossOrigin(origins = "*")
	public void deleteAllCommands(
			@RequestParam(name = "userSystemId", required = true) String systemId,
			@RequestParam(name = "userEmail", required = true) String email
			) {
		commandService.deleteAllCommands(systemId,email);
	}
		
	@GetMapping(
			path = { "/users" },
			produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(origins = "*")
	public UserBoundary[] ExportAllUsers(
			@RequestParam(name = "userSystemId", required = true) String systemId,
			@RequestParam(name = "userEmail", required = true) String email,
			@RequestParam(name = "size", defaultValue = "3", required = false) int size,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page
			) {
		return this.usersService
			.getAllUsers(size, page, systemId, email)
			.toArray(new UserBoundary[0]);
	}
	
	@GetMapping(
			path = { "/commands" }, 
            produces = { MediaType.APPLICATION_JSON_VALUE })
	@CrossOrigin(origins = "*")
    public CommandBoundary[] exportAllCommands(
    		@RequestParam(name = "userSystemId", required = true) String systemId,
			@RequestParam(name = "userEmail", required = true) String email,
			@RequestParam(name = "size", defaultValue = "3", required = false) int size,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page) {
        return this.commandService
        		.getAllCommandsHistory(size, page, systemId, email)
        		.toArray(new CommandBoundary[0]);
    }
}
