package hw01.ex1;

/**
 * Created by Lukas Dötlinger.
 */
public class TwoThreads implements Runnable{

  public void getThreadState(){
    System.out.println(Thread.currentThread().getName() + Thread.currentThread().getState() + " with priority: " +
      Thread.currentThread().getPriority());
  }

  public void run(){
    String tName = Thread.currentThread().getName();
    System.out.println(tName + " starting.");
    getThreadState();

  }

  public static void main(String[] args){
    TwoThreads tw = new TwoThreads();

    Thread t1 = new Thread(tw);
    Thread t2 = new Thread(tw);

    System.out.println(t1.getName() + t1.getState());
    System.out.println(t2.getName() + t2.getState());

    t1.start();
    t2.start();
  }

}
