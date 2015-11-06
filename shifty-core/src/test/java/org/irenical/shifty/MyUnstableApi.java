package org.irenical.shifty;

public class MyUnstableApi {
  
  private int slowMethodDelay;
  
  public MyUnstableApi(int slowMethodDelay) {
    this.slowMethodDelay=slowMethodDelay;
  }

  public String myRemoteMethod(Integer aNumber) {
    return aNumber.toString();
  }
  
  public String mySlowRemoteMethod(Integer aNumber) {
    try {
      Thread.sleep(slowMethodDelay);
    } catch (InterruptedException e) {
      return null;
    }
    return aNumber.toString();
  }
  
  public String myBrokenRemoteMethod(Integer aNumber) throws ContactSystemAdministratorException {
    throw new ContactSystemAdministratorException();
  }

}
