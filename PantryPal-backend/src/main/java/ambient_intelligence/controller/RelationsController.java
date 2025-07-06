package ambient_intelligence.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ambient_intelligence.boundary.ObjectBoundary;
import ambient_intelligence.boundary.ObjectChildIdBoundary;
import ambient_intelligence.logic.ObjectService;


@RestController
@RequestMapping(path = { "/ambient-intelligence/objects" })
@CrossOrigin(origins = "*")
public class RelationsController {
	private ObjectService objects;

	public RelationsController(ObjectService objects) {
		this.objects = objects;
	}
	
	@PutMapping(path = { "/{parentSystemID}/{parentObjectID}/children" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@CrossOrigin(origins = "*")
	public void bindObjects(
			@PathVariable("parentSystemID") String parentSystemID,
			@PathVariable("parentObjectID") String parentObjectID,
			@RequestParam(name = "userSystemID", required = true) String userSystemID, 
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestBody ObjectChildIdBoundary childIdBoundary) {
		this.objects
			.bindObjects(parentSystemID,parentObjectID,childIdBoundary.getSystemID(), childIdBoundary.getObjectId(), userSystemID, userEmail);
	}
	
	
	@GetMapping(path = { "/{childSystemID}/{childObjectID}/parents" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@CrossOrigin(origins = "*")
	public ObjectBoundary[] getParents(
			@PathVariable("childSystemID") String childSystemID,
			@PathVariable("childObjectID") String childObjectID,
			@RequestParam(name = "userSystemID", required = true) String userSystemID, 
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size, 
			@RequestParam(name = "page", required = false, defaultValue = "0") int page
			)
			 {
		return this.objects
			      .getParents(childSystemID, childObjectID, userSystemID, userEmail, size, page)
			      .toArray(new ObjectBoundary[0]);
		}
	
	@GetMapping(path = { "/{parentSystemID}/{parentObjectID}/children" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@CrossOrigin(origins = "*")
	public ObjectBoundary[] getChildren(
			@PathVariable("parentSystemID") String parentSystemID,
			@PathVariable("parentObjectID") String parentObjectID,
			@RequestParam(name = "userSystemID", required = true) String userSystemID, 
			@RequestParam(name = "userEmail", required = true) String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size, 
			@RequestParam(name = "page", required = false, defaultValue = "0") int page
			) {
		return this.objects
			.getChildren(parentSystemID,parentObjectID, userSystemID, userEmail, size, page)
			.toArray(new ObjectBoundary[0]);
	}

}
