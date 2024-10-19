package pro.trevor.joe.program.extern;

public class ExternGenerationException extends Exception {
    public ExternGenerationException(String message) {
        super(message);
    }

    public ExternGenerationException(ExternVariant variant, String message) {
        super("Unable to generate extern for " + variant.name() + ": " + message);
    }
}
