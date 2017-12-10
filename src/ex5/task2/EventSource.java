package ex5.task2;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Lukas Dötlinger.
 */
public class EventSource implements Runnable{

  private final AtomicReference<EventListener> listeners = new AtomicReference<EventListener>();

  public void registerListener(EventListener eventListener) {
    listeners.set(eventListener);
  }

  public void run() {
    while (true) {
      EventListener listener = listeners.getAndSet(null);
      if (listener != null) {
        listener.onEvent(null);
      }
    }
  }

}
