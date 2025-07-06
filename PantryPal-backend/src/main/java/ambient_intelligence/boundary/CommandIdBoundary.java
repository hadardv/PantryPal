package ambient_intelligence.boundary;

public class CommandIdBoundary {
	
	private String commandId;
    private String systemID;
    
	public CommandIdBoundary() {
		
	}
	
	public CommandIdBoundary(String commandId, String systemID) {
		this.commandId = commandId;
		this.systemID = systemID;
	}
	
	public String getCommandId() {
		return commandId;
	}
	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}
	public String getSystemID() {
		return systemID;
	}
	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}

	@Override
	public String toString() {
		return "{"
				+ "commandId: " + commandId 
				+ ", systemID: " + systemID 
				+ "}";
	}
    
    
    
}
