package ex2.task2;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class Producer extends Thread{

  private Buffer buffer;

  public Producer(Buffer buffer){
    this.buffer = buffer;
  }

  public ArrayList<Integer> generate(){
    return IntStream.generate(() -> ThreadLocalRandom.current().nextInt(0, 100 + 1))
      .takeWhile(i -> i > 0)
      .boxed()
      .collect(Collectors.toCollection(ArrayList::new));
  }

  public void run(){
    generate().stream()
      .forEach(i -> {this.buffer.add(i);
        System.out.println("Producer produced "+i);
      });

    try {
      sleep(ThreadLocalRandom.current().nextLong(0, 3 + 1));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
