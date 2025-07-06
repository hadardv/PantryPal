package ambient_intelligence.boundary;

import ambient_intelligence.data.UserRole;

public class UserBoundary {
	 
		private UserIdBoundary userId;
	    private UserRole role;
	    private String userName;
	    private String avatar;

	    public UserBoundary() {
			
		}

		public UserIdBoundary getUserId() {
			return userId;
		}

		public void setUserId(UserIdBoundary userId) {
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
					+ userId.toString() 
					+ "role: " + role
					+ " ,userName: " + userName
					+ " ,avatar: " + avatar
					+ "}";
		}
	    
	    

}
