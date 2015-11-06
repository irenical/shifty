package org.irenical.shifty;

public class ShiftyException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ShiftyException() {
    super();
  }

  public ShiftyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ShiftyException(String message, Throwable cause) {
    super(message, cause);
  }

  public ShiftyException(String message) {
    super(message);
  }

  public ShiftyException(Throwable cause) {
    super(cause);
  }

}
