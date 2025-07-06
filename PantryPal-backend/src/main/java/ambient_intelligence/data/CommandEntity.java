package ambient_intelligence.data;

import java.util.Date;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;


@Document(collection = "COMMANDS")
public class CommandEntity {
	@Id 
	private String commandId;
	private String command;
	private String targetObject;
	private Date invocationTimestamp;
	private String invokedBy;
    private Map<String, Object> commandAttributes; 
    
	public CommandEntity() {
		
	}

	public CommandEntity(String commandId, String command, String targetObject,
			Date invocationTimestamp, String invokedBy, Map<String, Object> commandAttributes) {
		this.commandId = commandId;
		this.command = command;
		this.targetObject = targetObject;
		this.invocationTimestamp = invocationTimestamp;
		this.invokedBy = invokedBy;
		this.commandAttributes = commandAttributes;
	}
	
	public String getCommandId() {
		return commandId;
	}
	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getTargetObject() {
		return targetObject;
	}
	public void setTargetObject(String targetObject) {
		this.targetObject = targetObject;
	}
	public Date getInvocationTimestamp() {
		return invocationTimestamp;
	}
	public void setInvocationTimestamp(Date invocationTimestamp) {
		this.invocationTimestamp = invocationTimestamp;
	}
	public String getInvokedBy() {
		return invokedBy;
	}
	public void setInvokedBy(String invokedBy) {
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
		return "{ commandId: " + commandId 
				+ ", command: " + command 
				+ ", targetObject: " + targetObject
				+ ", invocationTimestamp: " + invocationTimestamp 
				+ ", invokedBy: " + invokedBy 
				+ ", commandAttributes: " + commandAttributes 
				+ "}";
	}
    
	
    

}
