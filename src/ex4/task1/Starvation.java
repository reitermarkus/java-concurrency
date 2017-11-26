package ex4.task1;

public class Starvation {
  public static void main(String[] args) {
    final Object object = new Object();

    new Thread(() -> {
      while (true) {
        synchronized (object) {
          System.out.println("Thread 1 is doing some quick work.");
        }
      }
    }).start();

    new Thread(() -> {
      while (true) {
        synchronized (object) {
          System.out.println("Thread 2 is doing some lengthy work.");
          try { Thread.sleep(500);} catch (InterruptedException e) { e.printStackTrace(); }
        }
      }
    }).start();
  }
}
