package ex4.task2;

/**
 * Class representing a fork.
 */
public class Fork {

  private boolean available;
  private int rightP;
  private int leftP;

  public Fork(int size, int index){
    this.available = true;
    this.rightP = index;
    if ((size - index) == 1) {
      this.leftP = 0;
    } else {
      this.leftP = index + 1;
    }
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public int getRightP() {
    return rightP;
  }

  public int getLeftP() {
    return leftP;
  }
}
