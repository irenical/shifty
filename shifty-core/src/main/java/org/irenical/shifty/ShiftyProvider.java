package org.irenical.shifty;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ShiftyProvider<LOCATION,API> {
  
  private Supplier<List<LOCATION>> locator;

  private Function<List<LOCATION>, LOCATION> dispatcher;

  private Function<LOCATION, API> provider;


}
