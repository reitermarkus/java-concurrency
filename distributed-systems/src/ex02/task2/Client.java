package ex02.task2;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Client {
  private static Callable<String> createClient(final String inputString) {
    return () -> {
      var socket = new Socket("localhost", Protocol.PORT);
      var outputString = Protocol.request(socket, inputString);
      return outputString;
    };
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    var client1 = createClient("This is a string from client 1.");
    var client2 = createClient("This is a string from client 2.");
    var client3 = createClient("This is a string from client 3.");

    var exec = Executors.newFixedThreadPool(3);

    var clients = List.of(client1, client2, client3);

    exec.invokeAll(clients).forEach(c -> {
      try {
        System.out.println(c.get());
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    });

    exec.shutdown();
  }
}
