# lambda_event_api
A simple event api using java 8 lambdas and supporting inheritance

---
### Creating an Event
```java
public class ExampleEvent extends Event {
  private String msg;
  
  public ExampleEvent(String msg) {
    this.msg = msg;
  }
  
  public String getMessage() {
    return msg;
  }
}
```
---
### Creating a cancellable Event
```java
public class ExampleEvent extends Event implements Cancellable {
  private boolean cancelled;
  private String msg;
  
  public ExampleEvent(String msg) {
    this.msg = msg;
  }
  
  public String getMessage() {
    return msg;
  }
  
  @Override
  public boolean isCancelled() {
    return cancelled;
  }
  
  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
}
```
---
### Listening for an Event
```java
public class ExampleListener {

  public void onEvent(ExampleEvent event) {
    System.out.println(event.getMessage());
    
    //if ExampleEvent implements Cancellable, it can be cancelled:
    event.setCancelled(true);
  }
}
```
---
### Registering an Event
```java
EventManager eventManager = new EventManager();
ExampleListener l = new ExampleListener();
EventFunction<ExampleEvent> function = eventManager.registerEvent(l::onEvent, ExampleEvent.class);
```
---
### Unregistering an Event
```java
eventManager.unregisterEvent(function);
```
---
### Calling an Event
```java
ExampleEvent event = new ExampleEvent("Hello World!");
eventManager.call(event);

//if ExampleEvent implements Cancellable, the cancel status can be checked here:
if (event.isCancelled()) {
  //do something
} else {
  //do something else
}
```
---
### Inheritance and priority
If an Event inherits from another event, and functions for both events are registered, the hierarchically deepest
functions will be called first (first child, then parent).
Event functions are ordered by priority upon registration. The higher the priority, the later the function will be
called. Execution order given by inheritance cannot be changed by the priority.

---
### Async Event
An Event can be executed asynchronously. For this to work, a boolean of whether the Event is async or not has to be passed to Event when [creating an Event](#creating-an-event). If no `ExecutorService` has been given to the constructor of `EventManager`, it will automatically create a cached threadpool which is then used to execute an Event asynchronously. When using the automatically generated cached threadpool, the EventManager will terminate it automatically the moment the program exits.

If an Event is executed asynchronously, `EventManager#call()` will return a `Future<Boolean>` object that will return whether the Event has been cancelled or not as soon as it is done executing.
