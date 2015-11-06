package org.irenical.shifty;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;

public class ShiftyCall<API, RETURN> {
  
  private static final Logger LOG = LoggerFactory.getLogger(ShiftyCall.class);

  private Shifty<API> shifty;

  private ShiftyConfiguration<RETURN> conf;

  protected ShiftyCall(Shifty<API> shifty, ShiftyConfiguration<RETURN> conf) {
    this.conf = conf;
    this.shifty = shifty;
  }

  public ShiftyCall<API, RETURN> withFallback(Supplier<RETURN> fallback) {
    ShiftyConfiguration<RETURN> conf = new ShiftyConfiguration<RETURN>(this.conf);
    conf.setFallback(fallback);
    return new ShiftyCall<>(shifty, conf);
  }

  public ShiftyCall<API, RETURN> withTimeout(long timeoutMillis) {
    ShiftyConfiguration<RETURN> conf = new ShiftyConfiguration<RETURN>(this.conf);
    conf.setTimeoutMillis(timeoutMillis);
    return new ShiftyCall<>(shifty, conf);
  }
  
  public ShiftyCall<API, RETURN> withAutoClose() {
    ShiftyConfiguration<RETURN> conf = new ShiftyConfiguration<RETURN>(this.conf);
    conf.setAutoClose(true);
    return new ShiftyCall<>(shifty, conf);
  }

  public <ERROR extends Exception> RETURN call(ShiftyMethod<API, RETURN, ERROR> call) throws ERROR {
    try {
      return getHystrixCommand(call).execute();
    } catch (HystrixRuntimeException | HystrixBadRequestException e) {
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      } else if (e.getCause() != null) {
        throw (ERROR) e.getCause();
      } else {
        throw e;
      }
    }
  }

  private <ERROR extends Exception> HystrixCommand<RETURN> getHystrixCommand(ShiftyMethod<API, RETURN, ERROR> call) {
    int timeout = (int) conf.getTimeoutMillis();
    Setter setter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(shifty.getName()));
    if (timeout > 0) {
      setter = setter.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutEnabled(true).withExecutionTimeoutInMilliseconds(timeout));
    }
    HystrixCommand<RETURN> command = new HystrixCommand<RETURN>(setter) {

      private volatile Exception blewUp;

      @Override
      protected RETURN run() throws Exception {
        API got = shifty.getAPI();
        try {
          return call.apply(got);
        } catch (Exception e) {
          blewUp = e;
          throw e;
        } finally {
          if(conf.isAutoClose()){
            autoClose(got);
          }
        }
      }

      @Override
      protected RETURN getFallback() {
        if (conf.getFallback() != null) {
          return conf.getFallback().get();
        } else if (blewUp instanceof RuntimeException) {
          throw (RuntimeException) blewUp;
        } else {
          throw new RuntimeException(blewUp);
        }
      }
      
      private void autoClose(API got) throws Exception {
        if(got instanceof AutoCloseable){
          ((AutoCloseable) got).close();
        } else {
          throw new RuntimeException("Could not close instance of " + got.getClass() + ", as it does not implement AutoCloseable");
        }
      }
    };
    return command;
  }

  public <ERROR extends Exception> Future<RETURN> async(ShiftyMethod<API, RETURN, ERROR> call) {
    return shifty.getExecutor().submit(() -> call.apply(shifty.getAPI()));
  }

  public void async(Consumer<API> call) {
    shifty.getExecutor().execute(() -> call.accept(shifty.getAPI()));
  }

}
