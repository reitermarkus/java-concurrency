package ex5.task2;

import java.util.Date;

/**
 * Implementation where this escapes.
 */
public class ThisEscape {

  private final int num;

  public ThisEscape(EventSource source) {
    source.registerListener(
      e -> doSomething(e));

    num = 42;
  }

  private void doSomething(Event e) {
    if (num != 42) {
      System.out.println("Race condition detected at " + new Date());
    }
  }

  public static void main(String[] args) {
    EventSource es = new EventSource();

    Thread t = new Thread(es);
    t.start();

    while(true) {
      new ThisEscape(es);
    }
  }

}
