package org.irenical.shifty;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class Shifty<API> {

  private final Map<API, WeakReference<API>> proxies = Collections.synchronizedMap(new WeakHashMap<>());

  private final Callback interceptor = new MethodInterceptor() {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
      return proxy.invokeSuper(obj, args);
    }
  };

  private ExecutorService executorService = Executors.newCachedThreadPool();

  private Supplier<API> supplier;

  public Shifty() {
  }

  public void setSupplier(Supplier<API> supplier) {
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

  private API getStub() {
    API got = supplier.get();
    return getFromCache(got);
  }

  private API getFromCache(API got) {
    synchronized (got) {
      WeakReference<API> weakProxy = proxies.get(got);
      API proxy = weakProxy == null ? null : weakProxy.get();
      if (proxy == null) {
        proxy = wrap(got);
        proxies.put(got, new WeakReference<API>(proxy));
      }
      return proxy;
    }
  }

  @SuppressWarnings("unchecked")
  private API wrap(API got) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(got.getClass());
    enhancer.setClassLoader(got.getClass().getClassLoader());
    enhancer.setCallback(interceptor);
    return (API) enhancer.create();
  }
  
  public API call() {
    if (supplier == null) {
      throw new ShiftyException("This shifty instance has no provider. Set one before calling call() or async()");
    }
    return getStub();
  }

  public <RETURN> Future<RETURN> async(Function<API, RETURN> call) {
    if (executorService == null) {
      throw new RuntimeException("This shifty instance has no ExecutorService. Set one before calling async().");
    }
    return executorService.submit(() -> call.apply(call()));
  }
  
  public void async(Consumer<API> call) {
    if (executorService == null) {
      throw new RuntimeException("This shifty instance has no ExecutorService. Set one before calling async().");
    }
    executorService.execute(()->call.accept(call()));
  }

}
