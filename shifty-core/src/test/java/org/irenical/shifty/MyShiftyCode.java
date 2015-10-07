package org.irenical.shifty;

public class MyShiftyCode implements MyShiftyCodeInterface {

  public Integer aShiftyMethod(String got) {
    return Integer.parseInt(got);
  }
  
  public Integer someOtherShiftyMethod(String got) {
    return Integer.parseInt(got)/2;
  }

}
