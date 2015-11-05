package org.irenical.shifty;

public class MyUnstableApi {
  
  private int slowMethodDelay;
  
  public MyUnstableApi(int slowMethodDelay) {
    this.slowMethodDelay=slowMethodDelay;
  }

  public String myRemoteMethod(Integer aNumber) {
    return aNumber.toString();
  }
  
  public String mySlowRemoteMethod(Integer aNumber) throws InterruptedException{
    Thread.sleep(slowMethodDelay);
    return aNumber.toString();
  }

}
