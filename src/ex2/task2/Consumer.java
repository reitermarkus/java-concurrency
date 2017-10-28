package ex2.task2;

import static java.lang.Thread.sleep;

/**
 * Created by Lukas Dötlinger.
 */
public class Consumer implements Runnable {

  private Buffer buffer;

  public Consumer(Buffer buffer){
    this.buffer = buffer;
  }

  public void consume(){
    while(buffer.size() > 0){
      int e = buffer.get();
      System.out.println("Consumer consumed "+ e);
    }
  }

  public void run(){
    consume();

    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    consume();
  }
}
