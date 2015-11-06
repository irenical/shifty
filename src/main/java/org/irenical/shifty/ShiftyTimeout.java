package org.irenical.shifty;

public class ShiftyTimeout extends Exception {

  private static final long serialVersionUID = 1L;

  public ShiftyTimeout() {
    super();
  }

  public ShiftyTimeout(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ShiftyTimeout(String message, Throwable cause) {
    super(message, cause);
  }

  public ShiftyTimeout(String message) {
    super(message);
  }

  public ShiftyTimeout(Throwable cause) {
    super(cause);
  }
  
}
