package ambient_intelligence.service;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


//status = 401
@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class PantryUnauthorizedException extends RuntimeException{
	private static final long serialVersionUID = 1260486732893039411L;

	public PantryUnauthorizedException() {
		super();
	}
	
	public PantryUnauthorizedException(String message) {
		super(message);
	}
	
	public PantryUnauthorizedException(Exception cause) {
		super (cause);
	}
	
	public PantryUnauthorizedException(String message, Exception cause) {
		super(message, cause);
	}
}



