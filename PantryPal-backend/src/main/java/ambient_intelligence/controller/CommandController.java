package ambient_intelligence.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;

import ambient_intelligence.boundary.CommandBoundary;
import ambient_intelligence.logic.CommandService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ambient-intelligence/commands")
public class CommandController {
	private static final Log log = LogFactory.getLog(CommandController.class);
	
	private final CommandService commandService;

	public CommandController(CommandService commandService) {
		this.commandService = commandService;
	}
	
	@PostMapping(
			consumes = { MediaType.APPLICATION_JSON_VALUE }, 
    		produces = { MediaType.APPLICATION_JSON_VALUE })
	@CrossOrigin(origins = "*")
	public List<Object> invokeCommand(@RequestBody CommandBoundary command) {
		log.debug("invokeCommand(" + command.toString() + ")");
		return commandService.invokeCommand(command);
    }	
}
