package backend.exceptions;

/**
 * Exception thrown when there is an issue with placing an order.
 * <p>
 * This exception is used to indicate various problems that may arise during the
 * process of placing an order, such as an empty cart or other order-related errors.
 * </p>
 */
public class PurchaseOrderException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new {@code PurchaseOrderException} with {@code null} as its detail message.
     * The cause is not initialized.
     */
    public PurchaseOrderException() {
        super();
    }

    /**
     * Constructs a new {@code PurchaseOrderException} with the specified detail message.
     * The cause is not initialized.
     * 
     * @param message the detail message to be used for this exception.
     */
    public PurchaseOrderException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code PurchaseOrderException} with the specified cause.
     * The detail message is initialized to {@code cause.toString()}.
     * 
     * @param cause the cause of the exception to be used for this exception.
     */
    public PurchaseOrderException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code PurchaseOrderException} with the specified detail message and cause.
     * 
     * @param message the detail message to be used for this exception.
     * @param cause the cause of the exception to be used for this exception.
     */
    public PurchaseOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code PurchaseOrderException} with the specified detail message, cause,
     * suppression enabled or disabled, and writable stack trace enabled or disabled.
     * 
     * @param message the detail message to be used for this exception.
     * @param cause the cause of the exception to be used for this exception.
     * @param enableSuppression whether or not suppression is enabled or disabled.
     * @param writableStackTrace whether or not the stack trace should be writable.
     */
    public PurchaseOrderException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}