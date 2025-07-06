package ambient_intelligence.boundary;

import java.util.Map;

import ambient_intelligence.enums.ObjectTypeEnum;

import java.util.Date;


public class ObjectBoundary {
	private ObjectIdBoundary id;
	private ObjectTypeEnum type;
	private String alias;
	private String status;
	private Boolean active;
	private Date creationTimestamp;
	private UserIdBoundary createdBy;
	private Map<String, Object> objectDetails;
	
	public ObjectBoundary() {
		
	}
	
	public ObjectIdBoundary getId() {
		return id;
	}

	public void setId(ObjectIdBoundary id) {
		this.id = id;
	}

	public ObjectTypeEnum getType() {
		return type;
	}

	public void setType(ObjectTypeEnum type) {
		this.type = type;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public UserIdBoundary getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserIdBoundary createdBy) {
		this.createdBy = createdBy;
	}

	public Map<String, Object> getObjectDetails() {
		return objectDetails;
	}

	public void setObjectDetails(Map<String, Object> objectDetails) {
		this.objectDetails = objectDetails;
	}

	@Override
	public String toString() {
		return "{ id=" + id + ", type=" + type + ", alias=" + alias + ", status=" + status + ", active="
				+ active + ", creationTimestamp=" + creationTimestamp + ", createdBy=" + createdBy + ", objectDetails="
				+ objectDetails + "}";
	}
}