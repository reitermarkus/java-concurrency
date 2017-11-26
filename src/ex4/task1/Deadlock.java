package ex4.task1;

import java.util.concurrent.locks.*;

public class Deadlock {
  public static void main(String[] args) {
    final Object first = new Object();
    final Object second = new Object();

    new Thread(() -> {
      synchronized (first) {
        System.out.println("Thread 1 has locked the first object.");

        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        System.out.println("Thread 1 is trying to lock the second object.");

        synchronized (second) {
          System.out.println("Thread 1 has locked the second object.");
        }

      }
    }).start();

    new Thread(() -> {
      synchronized (second) {
        System.out.println("Thread 2 has locked the second object.");

        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        System.out.println("Thread 2 is trying to lock the first object.");

        synchronized (first) {
          System.out.println("Thread 2 has locked the first object.");
        }
      }

    }).start();
  }
}
