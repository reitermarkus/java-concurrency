package ex2.task2;

/**
 * Created by Lukas Dötlinger.
 */
public class MainTest{

  public static void main(String[] args) {

    Buffer buffer = new Buffer();
    Producer producer = new Producer(buffer);
    Consumer consumer = new Consumer(buffer);

    producer.start();
    consumer.start();
  }
}
