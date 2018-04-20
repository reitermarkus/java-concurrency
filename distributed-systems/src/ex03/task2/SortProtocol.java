package ex03.task2;

import java.rmi.*;

public interface SortProtocol extends Remote {
  String sort(String inputString) throws RemoteException;
}
