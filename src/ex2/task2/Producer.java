package ex2.task2;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Producer implements Runnable{

  private Buffer buffer;

  public Producer(Buffer buffer){
    this.buffer = buffer;
  }

  public IntStream generate(){
    return IntStream.generate(() -> ThreadLocalRandom.current().nextInt(0, 100 + 1))
      .takeWhile(i -> i > 0);
  }

  public void run(){
    generate()
      .forEachOrdered(i -> {
        this.buffer.add(i);
        System.out.println("Producer produced "+i);

        try {
          Thread.sleep(ThreadLocalRandom.current().nextLong(1000, 3000 + 1));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });

    buffer.setEndOfProduction(true);
  }

}
