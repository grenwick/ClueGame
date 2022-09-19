package clueGame;

public class BadConfigFormatException extends Exception {

	String errorMessage;

	public BadConfigFormatException() {
		super("The file is incorrectly formatted to play this game.");
	}
	
	public BadConfigFormatException(String error) {
		super(error);
		errorMessage = error;
	}
	
	@Override
	public String toString() {
		return errorMessage;
	}

}
