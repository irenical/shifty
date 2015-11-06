package org.irenical.shifty;

import java.util.function.Supplier;

public class ShiftyConfiguration<RETURN> {

  private long timeoutMillis;

  private Supplier<RETURN> fallback;

  private boolean autoClose;

  public ShiftyConfiguration() {
  }

  public ShiftyConfiguration(ShiftyConfiguration<RETURN> from) {
    this.timeoutMillis = from.timeoutMillis;
    this.fallback = from.fallback;
    this.autoClose = from.autoClose;
  }

  public void setFallback(Supplier<RETURN> fallback) {
    this.fallback = fallback;
  }

  public void setTimeoutMillis(long timeoutMillis) {
    this.timeoutMillis = timeoutMillis;
  }

  public Supplier<RETURN> getFallback() {
    return fallback;
  }

  public long getTimeoutMillis() {
    return timeoutMillis;
  }

  public void setAutoClose(boolean autoClose) {
    this.autoClose = autoClose;
  }

  public boolean isAutoClose() {
    return autoClose;
  }

}
