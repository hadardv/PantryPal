package ambient_intelligence.service;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// status = 403 
@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class PantryForbiddenException extends RuntimeException{
	private static final long serialVersionUID = 2095474562861475478L;

	public PantryForbiddenException() {
	}

	public PantryForbiddenException(String message) {
		super(message);
	}

	public PantryForbiddenException(Throwable cause) {
		super(cause);
	}

	public PantryForbiddenException(String message, Throwable cause) {
		super(message, cause);
	}
}
