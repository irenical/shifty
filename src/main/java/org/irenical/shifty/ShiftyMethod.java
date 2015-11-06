package org.irenical.shifty;

@FunctionalInterface
public interface ShiftyMethod<API, RETURN, ERROR extends Exception> {

  RETURN apply(API api) throws ERROR;

}
