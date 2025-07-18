package ambient_intelligence.boundary;

import ambient_intelligence.data.UserRole;

public class NewUserBoundary {
	
	private String email;
    private UserRole role;
    private String userName;
    private String avatar;

    public NewUserBoundary() {
    	
    }
    
    public NewUserBoundary(String email, UserRole role, String consumerName, String avatar) {
    	this.email = email;
    	this.role = role;
    	this.userName = consumerName;
    	this.avatar = avatar;
    }

    public String getEmail() { 
    	return email; 
    }
    
    public void setEmail(String email) { 
    	this.email = email; 
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

	public void setUserName(String consumerName) {
		this.userName = consumerName;
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
				+ "email: " + email + ","
				+ " ,role: " + role
				+ " ,consumerName: " + userName + ","
				+ " ,avatar: " + avatar
				+ "}";
	}
    
    

}
