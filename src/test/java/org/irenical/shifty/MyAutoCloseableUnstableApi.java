package org.irenical.shifty;

public class MyAutoCloseableUnstableApi extends MyUnstableApi implements AutoCloseable {

  private boolean isOpen = true;

  public MyAutoCloseableUnstableApi(int slowMethodDelay) {
    super(slowMethodDelay);
  }

  @Override
  public void close() throws Exception {
    isOpen = false;
  }

  public boolean isOpen() {
    return isOpen;
  }

}
