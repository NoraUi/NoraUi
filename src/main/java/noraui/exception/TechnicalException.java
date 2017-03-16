package noraui.exception;

public class TechnicalException extends Exception {

    /**
     * serialUid
     */
    private static final long serialVersionUID = -8316324009963694711L;

    public static final String TECHNICAL_ERROR_MESSAGE = "[ERROR] Erreur technique dans NoraUi: ";
    public static final String TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION = "Wrong data file (IOException, EmptyDataFileContentException or WrongDataFileFormatException)";
    public static final String TECHNICAL_ERROR_MESSAGE_CHECKSUM_IO_EXCEPTION = "NoraUi Security Exception (NoSuchAlgorithmException or IOException)";

    /**
     * Construit un objet <code>TechnicalException</code>.
     */
    public TechnicalException() {
        super();
    }

    /**
     * Construit un objet <code>TechnicalException</code>.
     *
     * @param message
     *            le message d'erreur
     * @param source
     *            l'exception source
     */
    public TechnicalException(String message, Throwable source) {
        super(message, source);
    }

    /**
     * Construit un objet <code>TechnicalException</code>.
     *
     * @param message
     *            le message d'erreur
     */
    public TechnicalException(String message) {
        super(message);
    }

    /**
     * Construit un objet <code>TechnicalException</code>.
     *
     * @param source
     *            l'exception source
     */
    public TechnicalException(Throwable source) {
        super(source);
    }

}