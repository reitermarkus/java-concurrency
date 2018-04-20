package ex3.task1;

public interface ReadWrite {
  void acquireRead();  // before read access
  void releaseRead();  // after read access
  void acquireWrite(); // before write access
  void releaseWrite(); // after write access
}
