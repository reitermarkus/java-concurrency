package ex2.task1;

/**
 * Created by Michael Kaltschmid on 28.10.2017.
 */
public class Volatile {

  private volatile int parameter = 0;

  public int getParameterValue() {
    return parameter;
  }

  public void setParameterValue(int parameter) {
    this.parameter = parameter;
  }

  public void addParameterValue() {
    this.parameter += 1;
  }

  public Volatile() {
    long startTime = System.currentTimeMillis();

    Thread thread1 = new Thread(() -> {
      addParameterValue();

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
      addParameterValue();

      synchronized (this) {
        notify();
      }
    });

    thread1.start();
    thread2.start();
  }

  public static void main(String[] args) {
    new Volatile();
  }
}
