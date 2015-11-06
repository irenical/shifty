package org.irenical.shifty;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Test;

public class ShiftyTest {

  @Test(expected = ShiftyException.class)
  public void testNoConnector() {
    Shifty<MyUnstableApi> shifty = new Shifty<MyUnstableApi>(null);
    shifty.call((api) -> api.myRemoteMethod(9001));
  }

  @Test
  public void testSimpleCall() throws InterruptedException, ExecutionException {
    Shifty<MyUnstableApi> shifty = new Shifty<>(() -> new MyUnstableApi(3000));
    String got = shifty.call((api) -> api.myRemoteMethod(9001));
    Assert.assertEquals(got, "9001");
  }

  @Test(expected = TimeoutException.class)
  public void testSlowCall() {
    Shifty<MyUnstableApi> shifty = new Shifty<>(() -> new MyUnstableApi(3000));
    ShiftyCall<MyUnstableApi, String> call = shifty.withTimeout(1000);
    call.call((api) -> api.mySlowRemoteMethod(9001));
  }

  @Test(expected = ContactSystemAdministratorException.class)
  public void testBrokenCall() throws ContactSystemAdministratorException {
    Shifty<MyUnstableApi> shifty = new Shifty<>(() -> new MyUnstableApi(3000));
    shifty.call((api) -> api.myBrokenRemoteMethod(9001));
  }
  
  @Test
  public void testBrokenCallFallback() throws ContactSystemAdministratorException {
    Shifty<MyUnstableApi> shifty = new Shifty<>(() -> new MyUnstableApi(3000));
    String got = shifty.withFallback(()->"8999").call((api) -> api.myBrokenRemoteMethod(9001));
    Assert.assertEquals(got, "8999");
  }

}
