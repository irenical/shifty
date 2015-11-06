# Shifty
[![Build Status](https://travis-ci.org/irenical/shifty.svg)](https://travis-ci.org/irenical/shifty)

## What for?
If you have a shady Java object, holding a bunch of methods you don't trust, where anything can go wrong. Instead of trying to wrap method calls in a bunch of try/catch blocks, separate thread, some timeout mechanism and a retry logic, you can use Shifty.
Shifty uses Hystrix internally to handle some of the work, extending these features to class level (instead of method level). This is specially usefull for class defined remote API.

## Usage
Say you have an object, an instance of the class MyUnstableApi, in some variable called myApi, and you don't trust it the least.

First wrap your object with Shifty
```java
Shifty<MyUnstableApi> shifty = new Shifty<>(()->myApi);
```
Notice a Supplier is passed on, not the actual instance. This allows you add complex logic, like service discovery or connection pooling to the retrieval of the actual API instance. Or simply dynamic instantiation, example bellow.
```java
Shifty<MyUnstableApi> shifty = new Shifty<>(()->new MyUnstableApi());
```

Now you have a shifty object prepared, you can now call arbitrary code over the instance retrieved by given supplier in the following way.
```java
String got = shifty.call((api)->api.myRemoteMethod(9001));
```
In this example, the API has a method that toStrings any integer... in some remote server. If something goes wrong, you will simply get an exception, as you would normally so pretty much nothing interesting happened so far. But now let's suppose you want to add a timeout rule to this call. You could write this instead.
```java
String got = shifty.withTimeout(1000).call((api)->api.myRemoteMethod(9001));
```
This call would giveup after 1 second.

