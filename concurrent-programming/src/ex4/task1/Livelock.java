package ex4.task1;

public class Livelock {
  static class DinnerTable {
    private boolean startedEating = false;

    public synchronized void someoneStartedEating() {
      this.startedEating = true;
    }

    public synchronized boolean hasSomeOneStartedEating() {
      return this.startedEating;
    }
  }

  public static void main(String[] args) {
    DinnerTable table = new DinnerTable();

    new Thread(() -> {
      while (!table.hasSomeOneStartedEating()) {
        System.out.println("Thread 1 says: Noone has started eating yet, so I'm just gonnga wait until someone else starts eating.");
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
      }

      table.someoneStartedEating();

      System.out.println("Thread 1 says: Someone has started eating, so I am eating now, too.");
    }).start();

    new Thread(() -> {
      while (!table.hasSomeOneStartedEating()) {
        System.out.println("Thread 2 says: Noone has started eating yet, so I'm just gonnga wait until someone else starts eating.");
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
      }

      table.someoneStartedEating();

      System.out.println("Thread 2 says: Someone has started eating, so I am eating now, too.");
    }).start();
  }
}
