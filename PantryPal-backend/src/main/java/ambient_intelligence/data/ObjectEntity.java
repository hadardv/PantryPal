package ambient_intelligence.data;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;
import ambient_intelligence.enums.ObjectTypeEnum;


@Document(collection = "OBJECTS")
public class ObjectEntity {
	@Id 
	private String id;
	private ObjectTypeEnum type;
	private String alias;
	private String status;
	private boolean active;
	private Date creationTimestamp;
	private String createdBy;
	private Map<String, Object> objectDetails;

	// Supports both one-to-many and many-to-many, depending on how the sets are populated
	@DBRef(lazy = true)
	private Set<ObjectEntity> parents = new HashSet<>();
	@DBRef(lazy = true)
	private Set<ObjectEntity> children = new HashSet<>();

	
	public ObjectEntity() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Map<String, Object> getObjectDetails() {
		return objectDetails;
	}

	public void setObjectDetails(Map<String, Object> objectDetails) {
		this.objectDetails = objectDetails;
	}
	
	public Set<ObjectEntity> getParents() {
		return parents;
	}

	public void setParents(Set<ObjectEntity> parents) {
		this.parents = parents;
	}

	public Set<ObjectEntity> getChildren() {
		return children;
	}

	public void setChildren(Set<ObjectEntity> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "{id=" + id + ", type=" + type + ", alias=" + alias + ", status=" + status + ", active="
				+ active + ", creationTimestamp=" + creationTimestamp + ", createdBy=" + createdBy + ", objectDetails="
				+ objectDetails + ", parents=" + parents + ", children=" + children + "}";
	}	
}