package ambient_intelligence.logic;

import java.util.List;
import java.util.Optional;

import ambient_intelligence.boundary.UserBoundary;

public interface UsersService {
	
	public UserBoundary createUser(UserBoundary userBoundary);

	public Optional<UserBoundary> login(String systemID, String email); 

	public void updateUser(String systemID, String email, UserBoundary update);
	
    public List<UserBoundary> getAllUsers(int size, int page, String systemId, String email);

	public void deleteAllUsers(String systemId, String email);

}
