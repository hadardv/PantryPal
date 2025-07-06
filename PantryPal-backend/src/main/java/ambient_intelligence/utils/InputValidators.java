package ambient_intelligence.utils;

import java.util.Objects;
import java.util.regex.Pattern;

import ambient_intelligence.data.UserRole;
import ambient_intelligence.enums.ObjectTypeEnum;

public class InputValidators {
	public static final Pattern EMAIL_PATTERN = 
			Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	public static boolean isValidSystemId(String systemID, String checkSystemID) {
		if (
				systemID == null ||
				systemID.isBlank() ||
				!(systemID.equals(checkSystemID))){			
			return false;}
		return true;
	}
	
	public static boolean isValidEmail(String email) {
		if (
				email == null ||
				email.isBlank() ||
				!EMAIL_PATTERN.matcher(email).matches()) {
			return false;}
		return true;
	}

	public static boolean isValidRole(String role) {
		return Objects.equals(role, UserRole.ADMIN.toString()) || Objects.equals(role, UserRole.END_USER.toString())
				|| Objects.equals(role, UserRole.OPERATOR.toString());
	}
	
	public static boolean isValidType(String type) {
		return Objects.equals(type, ObjectTypeEnum.INVENTORY.toString()) ||
				Objects.equals(type, ObjectTypeEnum.PRODUCT_BY_QUANTITY.toString()) || 
				Objects.equals(type, ObjectTypeEnum.PRODUCT_BY_WEIGHT.toString()) ||
				Objects.equals(type, ObjectTypeEnum.SHOPPING_LIST.toString());
	}

}