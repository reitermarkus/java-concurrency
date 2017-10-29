package ex2.task2;


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
    while(!buffer.isEndOfProduction()){
      consume();

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    consume();
  }
}
