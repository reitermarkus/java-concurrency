package ex5.task2;

import java.util.Date;

/**
 * Safe implementation where this does not escape.
 */
public class SafeListener {

  private final int num;
  private final EventListener listener;

  public SafeListener () {
    listener = e -> doSomething(e);

    num = 42;
  }

  private void doSomething(Event e) {
    if (num != 42) {
      System.out.println("Race condition detected at " + new Date());
    } else {
      System.out.println("This has not escaped at " + new Date());
    }
  }

  public static SafeListener newInstance(EventSource source) {
    SafeListener safe = new SafeListener();
    source.registerListener(safe.listener);
    return safe;
  }

  public static void main(String[] args) {
    EventSource es = new EventSource();

    Thread t = new Thread(es);
    t.start();

    while(true) {
      newInstance(es);
    }
  }

}
