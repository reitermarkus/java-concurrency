package ex03.task2;

import java.rmi.*;
import java.util.*;

public interface Protocol extends Remote {
  void compute(List<AccessLevel> accessLevels, Integer inputTime) throws RemoteException;
  String sort(List<AccessLevel> accessLevels, String inputString) throws RemoteException;
}
