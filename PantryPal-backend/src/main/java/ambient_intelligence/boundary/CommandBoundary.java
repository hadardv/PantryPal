package ambient_intelligence.boundary;

import java.util.Date;
import java.util.Map;

public class CommandBoundary {
	private CommandIdBoundary commandId;
	private String command;
    private ObjectIdBoundary targetObject;
    private Date invocationTimestamp;
    private UserIdBoundary invokedBy;
    private Map<String, Object> commandAttributes;
	
    public CommandBoundary() {
	}

	public CommandBoundary(CommandIdBoundary commandId, String command, ObjectIdBoundary targetObject,
			Date invocationTimestamp, UserIdBoundary invokedBy, Map<String, Object> commandAttributes) {
		
		this.commandId = commandId;
		this.command = command;
		this.targetObject = targetObject;
		this.invocationTimestamp = invocationTimestamp;
		this.invokedBy = invokedBy;
		this.commandAttributes = commandAttributes;
	}

	public CommandIdBoundary getCommandId() {
		return commandId;
	}

	public void setCommandId(CommandIdBoundary commandId) {
		this.commandId = commandId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public ObjectIdBoundary getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(ObjectIdBoundary targetObject) {
		this.targetObject = targetObject;
	}

	public Date getInvocationTimestamp() {
		return invocationTimestamp;
	}

	public void setInvocationTimestamp(Date invocationTimestamp) {
		this.invocationTimestamp = invocationTimestamp;
	}

	public UserIdBoundary getInvokedBy() {
		return invokedBy;
	}

	public void setInvokedBy(UserIdBoundary invokedBy) {
		this.invokedBy = invokedBy;
	}

	public Map<String, Object> getCommandAttributes() {
		return commandAttributes;
	}

	public void setCommandAttributes(Map<String, Object> commandAttributes) {
		this.commandAttributes = commandAttributes;
	}

	@Override
	public String toString() {
		return "{commandId: " + commandId 
				+ ", command: " + command 
				+ ", targetObject: " + targetObject
				+ ", invocationTimestamp: " + invocationTimestamp 
				+ ", invokedBy: " + invokedBy 
				+ ", commandAttributes: " + commandAttributes 
				+ "}";
	}
}
