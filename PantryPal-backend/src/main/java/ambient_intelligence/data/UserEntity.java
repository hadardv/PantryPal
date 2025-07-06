package ambient_intelligence.data;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "USERS")
public class UserEntity {
	@Id
	private String userId;
	private UserRole role;
	private String userName;
	private String avatar;
	
	
	public UserEntity() {
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Override
	public String toString() {
		return "{"
				+ "id: " + userId.toString() 
				+ " ,role: " + role 
				+ " ,userName: " + userName 
				+ " ,avatar: " + avatar
				+ "}";
	}

    
}
