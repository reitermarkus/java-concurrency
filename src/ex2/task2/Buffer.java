package ex2.task2;

import java.util.ArrayList;

/**
 * Created by Lukas Dötlinger.
 */
public class Buffer {

  private ArrayList<Integer> buffer;

  public Buffer(){
    buffer = new ArrayList<>();
  }

  public synchronized Integer get(){
    if(buffer.size() > 0) {
      return buffer.remove(buffer.size() - 1);
    } else {
      return -1;
    }
  }

  public synchronized void add(int i){
    buffer.add(i);
  }

  public synchronized Integer size(){
    return buffer.size();
  }

}
