package org.irenical.shifty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Shifty<API> {
  
  private ExecutorService executorService;

  private Supplier<API> supplier;

  public Shifty(Supplier<API> supplier) {
    setSupplier(supplier);
  }

  public void setSupplier(Supplier<API> supplier) {
    if (supplier == null) {
      throw new ShiftyException("This shifty instance has no provider. Set one before calling call() or async()");
    }
    this.supplier = supplier;
  }

  public Supplier<API> getSupplier() {
    return supplier;
  }

  public void setExecutorService(ExecutorService executorService) {
    this.executorService = executorService;
  }

  public ExecutorService getExecutorService() {
    return executorService;
  }
  
  protected String getName() {
    return supplier.getClass().getName();
  }

  public <RETURN> ShiftyCall<API, RETURN> withFallback(Supplier<RETURN> fallback) {
    ShiftyConfiguration<RETURN> conf = new ShiftyConfiguration<>();
    conf.setFallback(fallback);
    return new ShiftyCall<>(this, conf);
  }

  public <RETURN> ShiftyCall<API, RETURN> withTimeout(long timeoutMillis) {
    ShiftyConfiguration<RETURN> conf = new ShiftyConfiguration<>();
    conf.setTimeoutMillis(timeoutMillis);
    return new ShiftyCall<>(this, conf);
  }

  public <RETURN, ERROR extends Exception> RETURN call(ShiftyMethod<API, RETURN, ERROR> call) throws ERROR {
    return new ShiftyCall<>(this, new ShiftyConfiguration<RETURN>()).call(call);
  }

  public <RETURN, ERROR extends Exception> Future<RETURN> async(ShiftyMethod<API, RETURN, ERROR> call) throws ERROR {
    return new ShiftyCall<>(this, new ShiftyConfiguration<RETURN>()).async(call);
  }

  public void async(Consumer<API> call) {
    new ShiftyCall<>(this, new ShiftyConfiguration<>()).async(call);
  }

  protected API getAPI() {
    return supplier.get();
  }

  protected ExecutorService getExecutor() {
    if (executorService == null) {
      executorService = Executors.newCachedThreadPool();
    }
    return getExecutorService();
  }

}
