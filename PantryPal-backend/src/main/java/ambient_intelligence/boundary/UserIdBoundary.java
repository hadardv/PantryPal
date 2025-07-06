package ambient_intelligence.boundary;

public class UserIdBoundary {
	private String email;
    private String systemID;
    
    public UserIdBoundary() {
		
	}
    
    public UserIdBoundary(String email, String systemID) {
		this.email = email;
		this.systemID = systemID;
	}
    
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSystemID() {
		return systemID;
	}

	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}

	@Override
	public String toString() {
		return "ConsumerIdBoundaryy [email=" + email + ", systemID=" + systemID + "]";
	}

}
