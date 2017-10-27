package ex2.task2;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Lukas Dötlinger.
 */
public class Consumer extends Thread {

  private Buffer buffer;

  public Consumer(Buffer buffer){
    this.buffer = buffer;
  }

  public void consume(){
    while(buffer.size() > 0){
      System.out.println("Consumer consumed "+buffer.get());
    }
  }

  public void run(){
    consume();

    try {
      sleep(2);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    consume();
  }
}
