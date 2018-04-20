package ex02.task1;

import ex02.task2.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
  Server() throws IOException {
    var socket = new ServerSocket(Protocol.PORT);
    var exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    while (true) {
      try {
        var connection = socket.accept();

        System.out.println("Client connected from " + socket.getInetAddress() + ".");

        exec.submit(() -> {
          try {
            Protocol.reply(connection);

            if (connection.isClosed()) {
              socket.close();
            }
          } catch (IOException e) {
            // Ignore.
          }
        });
      } catch (SocketException e) {
        System.out.println("Shutting down ...");
        break;
      }
    }

    exec.shutdown();
    System.out.println("Exiting ...");
  }

  public static void main(String[] args) throws IOException {
    new Server();
  }
}
