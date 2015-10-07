package org.irenical.shifty;

import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;

public class ShiftyTest {

  @Test(expected = ShiftyException.class)
  public void testNoConnector() {
    Shifty<MyShiftyCode> shifty = new Shifty<MyShiftyCode>();
    shifty.call();
  }

  public class MyUnstableApi {

    public String myRemoteMethod(Integer aNumber) {
      return aNumber.toString();
    }

  }

  @Test
  public void testInterface() {
    // a supplier can be a very complex thing, service discovery and pooling
    // capabilities
    // shoud be address in the supplier implementation
    Supplier<MyUnstableApi> supplier = () -> new MyUnstableApi();

    // instantiation
    Shifty<MyUnstableApi> shifty = new Shifty<MyUnstableApi>();
    shifty.setSupplier(supplier);
    // end instantiation

    // synchronous call. The method call() will return a proxied version of the
    // API
    // object (the one returned by the supplier)
    String got = shifty.call().myRemoteMethod(9001);

    // asynchronous call
    shifty.async((api) -> {
      got = api.myRemoteMethod(9001);
      Assert.assertEquals(got, "9001");
    });

    // future call
    Future<String> future = shifty.async((api) -> {
      return api.myRemoteMethod(9001);
    });
    
    Assert.assertEquals(future.get(), "9001");

  }

  // @Test
  // public void testClass() {
  // Shifty<MyShiftyCode> shifty = new Shifty<MyShiftyCode>();
  // shifty.setProvider(()->new MyShiftyCode());
  // testCall(shifty);
  // }
  //
  // private void testCall(Shifty<? extends MyShiftyCodeInterface> shifty) {
  // Integer got = shifty.call().aShiftyMethod("6");
  // Assert.assertEquals(got, (Integer) 6);
  // got = shifty.call().someOtherShiftyMethod("6");
  // Assert.assertEquals(got, (Integer) 3);
  //
  // shifty.async().
  // }
}
