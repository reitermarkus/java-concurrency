package ex1.task1;

public class MaxThreads {
  public static void main(String[] args) {
    int counter = 0;

    while (true) {
      try {
        new Thread(() -> {
          while (true) {
            Thread.yield();
          }
        }).start();
        counter++;
      } catch (OutOfMemoryError e) {
        System.out.println("Your system can handle " + counter + " threads.");
        System.exit(0);
      }
    }
  }
}
