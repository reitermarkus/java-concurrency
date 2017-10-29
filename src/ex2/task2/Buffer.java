package ex2.task2;

import java.util.ArrayList;


public class Buffer {

  private ArrayList<Integer> buffer;
  private boolean endOfProduction;

  public Buffer(){
    buffer = new ArrayList<>();
    endOfProduction = false;
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

  public boolean isEndOfProduction() {
    return endOfProduction;
  }

  public void setEndOfProduction(boolean endOfProduction) {
    this.endOfProduction = endOfProduction;
  }
}
