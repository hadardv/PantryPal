package ambient_intelligence.service;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// status=404
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class PantryNotFoundException extends RuntimeException{
	private static final long serialVersionUID = -4216411184752620113L;

	public PantryNotFoundException() {
		super();
	}
	
	public PantryNotFoundException(String message) {
		super(message);
	}
	
	public PantryNotFoundException(Exception cause) {
		super (cause);
	}
	
	public PantryNotFoundException(String message, Exception cause) {
		super(message, cause);
	}
}


