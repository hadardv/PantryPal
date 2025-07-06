package ambient_intelligence.converters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ambient_intelligence.boundary.NewUserBoundary;
import ambient_intelligence.boundary.UserBoundary;
import ambient_intelligence.boundary.UserIdBoundary;
import ambient_intelligence.data.UserEntity;


@Component
public class UserConverter {
	@Value("${spring.application.name}")
    private String systemID;
	
	public UserBoundary toBoundary(UserEntity entity) {
		UserBoundary boundary = new UserBoundary();
		
		UserIdBoundary userId = new UserIdBoundary();
		if (entity.getUserId()!= null) {
			String[] idParts = entity.getUserId().split("_");
			userId.setEmail(idParts[1]);
			userId.setSystemID(idParts[0]);
		}
		
		boundary.setRole(entity.getRole());
		boundary.setUserId(userId);
		boundary.setUserName(entity.getUserName());
		boundary.setAvatar(entity.getAvatar());

		return boundary;		
	}
	
	public UserEntity toEntity(UserBoundary boundary) {
		UserEntity entity = new UserEntity();
		
		if (boundary.getUserId() != null) {
			entity.setUserId(boundary.getUserId().getSystemID() + "_" + boundary.getUserId().getEmail());
		}
		else {
			entity.setUserId(null);
		}
		
		if (boundary.getRole() != null) {
			entity.setRole(boundary.getRole());

		}

		entity.setUserName(boundary.getUserName());
		entity.setAvatar(boundary.getAvatar());
		return entity;
	}

		
	public UserBoundary newUsertoUserBoundary(NewUserBoundary newUserBoundary) {
		UserBoundary boundary = new UserBoundary();
		UserIdBoundary userId = new UserIdBoundary(newUserBoundary.getEmail(), this.systemID);
		boundary.setUserId(userId);
		boundary.setUserName(newUserBoundary.getUserName());
		boundary.setAvatar(newUserBoundary.getAvatar());
		boundary.setRole(newUserBoundary.getRole());

		return boundary;
	}
}
