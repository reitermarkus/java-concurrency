package ex2.task1;

/**
 * Created by Michael Kaltschmid on 28.10.2017.
 */
public class Sync {

  private int parameter = 0;

  public synchronized int getParameterValue() {
    return parameter;
  }

  public synchronized void setParameterValue(int parameter) {
    this.parameter = parameter;
  }

  public synchronized void addParameterValue() {
    this.parameter += 1;
  }

  public Sync() {
    long startTime = System.currentTimeMillis();

    Thread thread1 = new Thread(() -> {
      setParameterValue(3);

      synchronized (this) {
          try {
            wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
      }

      System.out.println(getParameterValue());
      System.out.println(("The operation took " + (System.currentTimeMillis() - startTime) + "ms"));
    });

    Thread thread2 = new Thread(() -> {
      setParameterValue(2);

      synchronized (this) {
        notify();
      }
    });

    thread1.start();
    thread2.start();
  }

  public static void main(String[] args) {
    new Sync();
  }
}
