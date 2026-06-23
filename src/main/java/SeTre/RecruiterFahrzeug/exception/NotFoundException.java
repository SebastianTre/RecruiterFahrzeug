package SeTre.RecruiterFahrzeug.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Objekt nicht gefunden");
    }
}
