package ambient_intelligence.logic;
import java.util.List;
import java.util.Optional;

import ambient_intelligence.boundary.ObjectBoundary;
import ambient_intelligence.enums.ObjectTypeEnum;

public interface ObjectService {
	public ObjectBoundary create(ObjectBoundary object);
	
	public void updateObject(String systemID, String id, ObjectBoundary update, String userSystemId, String userEmail);
	
	public Optional<ObjectBoundary> getSpecificObject(String systemID,String id, String userSystemId, String userEmail);
	
	public List<ObjectBoundary> getAllObjects(String userSystemID, String userEmail, int size, int page);
	
	public void deleteAllObjects(String systemId, String email);
	
	public void bindObjects(
			  String parentSystemID,
			  String parentObjectID,
			  String childSystemID,
			  String childObjectID,
			  String userSystemID, 
			  String userEmail
			);
	
	public List<ObjectBoundary> getParents(
			String childSystemID,
			String childObjectID,
			String userSystemID,
			String userEmail,
			int size,
			int page);

	public List<ObjectBoundary> getChildren(
			String parentSystemID,
			String parentObjectID,
			String userSystemID,
			String userEmail,
			int size,
			int page);
	
	
	public List<ObjectBoundary> searchByAlias(
			String alias, 
			int size, int page, String systemID, String email);

	public List<ObjectBoundary> searchByAliasPattern(
			String pattern, 
			int size, int page, String systemID, String email);

	public List<ObjectBoundary> searchByType(
			ObjectTypeEnum type, 
			int size, int page, String systemID, String email);
	
	public List<ObjectBoundary> searchByStatus(
			String status, 
			int size, int page, String systemID, String email);
	
	public List<ObjectBoundary> searchByTypeAndStatus(
			ObjectTypeEnum type, 
			String status, 
			int size, int page, String systemID, String email);
	        
}
