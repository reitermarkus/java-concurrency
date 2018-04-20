package ex03.task1;

import java.rmi.*;

public interface Protocol extends Remote {
  String sort(String inputString) throws RemoteException;
  void compute(Integer inputTime) throws RemoteException;
}
