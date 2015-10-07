package org.irenical.shifty;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;

public class ShiftyTest {

  @Test(expected = ShiftyException.class)
  public void testNoConnector() {
    Shifty<MyUnstableApi> shifty = new Shifty<MyUnstableApi>();
    shifty.call();
  }

  @Test
  public void testInterface() throws InterruptedException, ExecutionException {
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
    Assert.assertEquals(got, "9001");
    
    // asynchronous call
    shifty.async((api) -> {
      String asyncGot = api.myRemoteMethod(9001);
      Assert.assertEquals(asyncGot, "9001");
    });

    // future call
    Future<String> future = shifty.async((api) -> {
      return api.myRemoteMethod(9001);
    });
    
    Assert.assertEquals(future.get(), "9001");

  }

}
