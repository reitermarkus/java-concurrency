package ex2.task2;

/**
 * Created by Lukas Dötlinger.
 */
public class MainTest{

  public static void main(String[] args) {

    Buffer buffer = new Buffer();
    Producer producer = new Producer(buffer);
    Consumer consumer = new Consumer(buffer);

    Thread p = new Thread(producer);
    Thread c = new Thread(consumer);

    p.start();
    c.start();
  }
}
