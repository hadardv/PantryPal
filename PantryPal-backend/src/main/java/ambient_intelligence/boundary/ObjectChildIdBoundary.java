package ambient_intelligence.boundary;

public class ObjectChildIdBoundary {
	private String objectId;
    private String systemID;
    
	public ObjectChildIdBoundary() {
		
	}
	
	public ObjectChildIdBoundary(String objectId, String systemID) {
		this.objectId = objectId;
		this.systemID = systemID;
	}
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getSystemID() {
		return systemID;
	}

	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}
}
