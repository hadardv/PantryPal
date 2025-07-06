package ambient_intelligence.controller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ambient_intelligence.boundary.ObjectBoundary;
import ambient_intelligence.enums.ObjectTypeEnum;
import ambient_intelligence.logic.ObjectService;
import ambient_intelligence.service.ObjectServiceImpl;

@RestController
@RequestMapping(path = { "/ambient-intelligence/objects" })
public class ObjectController {
	private ObjectService objectService;
	private Log log = LogFactory.getLog(ObjectServiceImpl.class);

	public ObjectController(ObjectService objectService) {
		this.objectService = objectService;
	}

	@PostMapping(
		consumes = { MediaType.APPLICATION_JSON_VALUE }, 
		produces = { MediaType.APPLICATION_JSON_VALUE })
	@CrossOrigin(origins = "*")
	public ObjectBoundary createObject(@RequestBody ObjectBoundary object) {
		this.log.info("createObject(" + object + ")");
		return this.objectService
			.create(object);
	}

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
	@CrossOrigin(origins = "*")
	public ObjectBoundary[] getAllObjects(
			@RequestParam(name = "userSystemID", required = true) String userSystemID, 
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size, 
			@RequestParam(name = "page", required = false, defaultValue = "0") int page
			){
		System.out.println("Request received for getAllObjects");
		return this.objectService
			.getAllObjects(userSystemID, userEmail, size, page)
			.toArray(new ObjectBoundary[0]);		
	}
	
	@GetMapping(
			path = {"/{systemID}/{id}"},  
			produces = { MediaType.APPLICATION_JSON_VALUE })
	@CrossOrigin(origins = "*")
		public ObjectBoundary getSingleInstance(
				@PathVariable("systemID") String systemID,
				@PathVariable("id") String id,
				@RequestParam(name = "userSystemID", required = true) String userSystemID, 
				@RequestParam(name = "userEmail", required = true) String userEmail)
				{
			return this.objectService
				.getSpecificObject(systemID, id, userSystemID, userEmail)
				.orElseThrow(()-> 
					new RuntimeException("could not find object with id: " + id + "and systemId" + systemID)
				);
		}
	
	@PutMapping(path = {"/{systemID}/{id}"}, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@CrossOrigin(origins = "*")
	public void updateObject(
			@PathVariable("systemID") String systemID,
			@PathVariable("id") String id,  
			@RequestParam(name = "userSystemID", required = true) String userSystemID, 
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestBody ObjectBoundary update) {
		this.objectService
			.updateObject(systemID, id, update, userSystemID, userEmail);
	}
	
	
	
	@GetMapping(path ={"search/byAlias/{alias}"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
		@CrossOrigin(origins = "*")
		public ObjectBoundary[] searchByAlias(
				@PathVariable("alias") String alias,
				@RequestParam(name = "userSystemID", required = true) String userSystemID, 
				@RequestParam(name = "userEmail", required = true) String userEmail,
				@RequestParam(name = "size", required = false, defaultValue = "10") int size, 
				@RequestParam(name = "page", required = false, defaultValue = "0") int page
				) {
					
			return this.objectService
			.searchByAlias(alias, size, page, userSystemID, userEmail)
			.toArray(new ObjectBoundary[0]);
		}
		
		
		@GetMapping(path ={"search/byAliasPattern/{pattern}"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
		@CrossOrigin(origins = "*")
		public ObjectBoundary[] searchByAliasPattern(
				@PathVariable("pattern") String pattern,
				@RequestParam(name = "userSystemID", required = true) String userSystemID, 
				@RequestParam(name = "userEmail", required = true) String userEmail,
				@RequestParam(name = "size", required = false, defaultValue = "10") int size, 
				@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
			
			return this.objectService
			.searchByAliasPattern(pattern, size, page, userSystemID, userEmail)
			.toArray(new ObjectBoundary[0]);
		}
		
		
		@GetMapping(path ={"search/byType/{type}"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
		@CrossOrigin(origins = "*")
		public ObjectBoundary[] searchByType(
				@PathVariable("type") ObjectTypeEnum type,
				@RequestParam(name = "userSystemID", required = true) String userSystemID, 
				@RequestParam(name = "userEmail", required = true) String userEmail,
				@RequestParam(name = "size", required = false, defaultValue = "10") int size, 
				@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
			
			return this.objectService
			.searchByType(type, size, page, userSystemID, userEmail)
			.toArray(new ObjectBoundary[0]);
		}
		
				
		@GetMapping(path ={"search/byStatus/{status}"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
		@CrossOrigin(origins = "*")
		public ObjectBoundary[] searchByStatus(
				@PathVariable("status") String status,
				@RequestParam(name = "userSystemID", required = true) String userSystemID, 
				@RequestParam(name = "userEmail", required = true) String userEmail,
				@RequestParam(name = "size", required = false, defaultValue = "10") int size, 
				@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

			return this.objectService
			.searchByStatus(status, size, page, userSystemID, userEmail)
			.toArray(new ObjectBoundary[0]);
		}
		
		
		@GetMapping(path ={"search/byTypeAndStatus/{type}/{status}"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
		@CrossOrigin(origins = "*")
		public ObjectBoundary[] searchByTypeAndStatus(
				@PathVariable("type") ObjectTypeEnum type,
				@PathVariable("status") String status,
				@RequestParam(name = "userSystemID", required = true) String userSystemID, 
				@RequestParam(name = "userEmail", required = true) String userEmail,
				@RequestParam(name = "size", required = false, defaultValue = "10") int size, 
				@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

			return this.objectService
			.searchByTypeAndStatus(type, status, size, page, userSystemID, userEmail)
			.toArray(new ObjectBoundary[0]);
		}
}