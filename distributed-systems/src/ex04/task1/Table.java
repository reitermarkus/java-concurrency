package ex04.task1;

import javafx.util.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;

public class Table implements Serializable {
  private static final long serialVersionUID = 1L;

  private Map<String, InetSocketAddress> table = new HashMap<>();

  private Node node;

  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    out.writeObject(this.table);
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    this.table = (Map<String, InetSocketAddress>)in.readObject();
  }

  public Table(Node node) {
    this.node = node;
  }

  public boolean add(String name, InetSocketAddress socketAddress) {
    if (this.table.containsKey(name)) {
      return false;
    }

    final var address = socketAddress.getAddress();
    final var port = socketAddress.getPort();

    final var isLoopback = address.isLoopbackAddress();
    var isLocalhost = false;
    try {
      isLocalhost = address.equals(InetAddress.getLocalHost());
    } catch (UnknownHostException e) {}

    if ((isLocalhost || isLoopback || address.equals(this.node.getAddress())) && port == this.node.getPort()) {
      // Do not add this node's own address.
      return false;
    }

    this.table.put(name, socketAddress);
    return true;
  }

  public Pair<List<Map.Entry<String, InetSocketAddress>>, List<Map.Entry<String, InetSocketAddress>>> merge(Table other) {
    return this.merge(other.table.entrySet());
  }

  public boolean merge(String name, InetSocketAddress socketAddress) {
    final var entry = Map.entry(name, socketAddress);
    final var added = this.merge(List.of(entry)).getKey();
    return added.contains(entry);
  }

  private Pair<List<Map.Entry<String, InetSocketAddress>>, List<Map.Entry<String, InetSocketAddress>>> merge(Collection<Map.Entry<String, InetSocketAddress>> newEntries) {
    List<Map.Entry<String, InetSocketAddress>> added = new ArrayList<>();
    List<Map.Entry<String, InetSocketAddress>> removed = new ArrayList<>();

    newEntries.forEach(entry -> {
      if (this.add(entry.getKey(), entry.getValue())) {
        added.add(entry);
      }
    });

    var entries = this.table.entrySet().stream().collect(Collectors.toList());
    Collections.shuffle(entries);

    entries.stream().skip(Math.min(this.size(), this.node.getNetworkSize())).forEach(entry -> {
     if (!added.remove(entry)) {
       removed.add(entry);
     }

      this.remove(entry.getKey());
    });

    return new Pair<>(added, removed);
  }

  public boolean remove(String name) {
    if (!this.table.containsKey(name)) {
      return false;
    }

    this.table.remove(name);
    return true;
  }

  public Optional<Peer> getRandom() {
    var entries = this.table.entrySet().stream().collect(Collectors.toList());
    Collections.shuffle(entries);

    return entries.stream().findFirst().map(peer -> new Peer(peer));
  }

  public boolean contains(String name) {
    return this.table.containsKey(name);
  }

  public InetSocketAddress get(String name) {
    return this.table.get(name);
  }

  public Set<Map.Entry<String, InetSocketAddress>> getEntrySet() {
    return table.entrySet();
  }

  public int size() {
    return this.table.size();
  }

  @Override
  public String toString() {
    return this.table.keySet().toString();
  }
}
