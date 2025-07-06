package ambient_intelligence.service;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


//status=400
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PantryInvalidInputException extends RuntimeException{
	private static final long serialVersionUID = 4462801345175682533L;

	public PantryInvalidInputException() {
	}

	public PantryInvalidInputException(String message) {
		super(message);
	}

	public PantryInvalidInputException(Throwable cause) {
		super(cause);
	}

	public PantryInvalidInputException(String message, Throwable cause) {
		super(message, cause);
	}
}