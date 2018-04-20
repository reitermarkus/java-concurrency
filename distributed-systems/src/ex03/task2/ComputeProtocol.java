package ex03.task2;

import java.rmi.*;

public interface ComputeProtocol extends Remote {
  void compute(Integer inputTime) throws RemoteException;
}
