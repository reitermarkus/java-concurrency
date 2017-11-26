package ex4.task3;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Michael Kaltschmid on 25.11.2017.
 */
public class ConcurrentSort {
  public static <T extends Comparable<? super T>> void insertSort(List<T> list) {
    for (int i = 1; i < list.size(); i++) {
      T value = list.get(i);
      int j = i - 1;

      while (j >= 0 && list.get(j).compareTo(value) < 0) {
        list.set(j + 1, list.get(j));
        j = j - 1;
      }

      list.set(j + 1, value);
    }
  }

  public static <T extends Comparable<? super T>> void selectionSort(List<T> list) {
    for (int i = 0; i < list.size() - 1; i++) {
      int min = i;

      for (int j = i + 1; j < list.size(); j++) {
        if (list.get(min).compareTo((list.get(j))) > 0) {
          min = j;
        }
      }

      if(min != i) {
        T swap = list.get(min);
        list.set(min, list.get(i));
        list.set(i, swap);
      }
    }
  }

  public static <T extends Comparable<? super T>> List<T> quickSort(List<T> list) {
    if (!list.isEmpty()) {
      T pivot = list.get(0);


      List<T> less = new LinkedList<T>();
      List<T> pivotList = new LinkedList<T>();
      List<T> more = new LinkedList<T>();

      for (T i : list) {
        if (i.compareTo(pivot) < 0)
          less.add(i);
        else if (i.compareTo(pivot) > 0)
          more.add(i);
        else
          pivotList.add(i);
      }

      less = quickSort(less);
      more = quickSort(more);

      less.addAll(pivotList);
      less.addAll(more);
      return less;
    }
    return list;
  }

  public static <T extends Comparable<? super T>> List<T> mergeSort(List<T> list) {
    if (list.size() <= 1)
      return list;

    int middle = list.size() / 2;
    List<T> left = list.subList(0, middle);
    List<T> right = list.subList(middle, list.size());

    right = mergeSort(right);
    left = mergeSort(left);
    List<T> result = merge(left, right);

    return result;
  }

  public static <T extends Comparable<? super T>> List<T> merge(List<T> left, List<T> right) {
    List<T> result = new ArrayList<T>();
    Iterator<T> it1 = left.iterator();
    Iterator<T> it2 = right.iterator();

    T x = it1.next();
    T y = it2.next();

    while (true) {
      if (x.compareTo(y) <= 0) {
        result.add(x);
        if (it1.hasNext()) {
          x = it1.next();
        } else {
          result.add(y);
          while (it2.hasNext()) {
            result.add(it2.next());
          }
          break;
        }
      } else {
        result.add(y);
        if (it2.hasNext()) {
          y = it2.next();
        } else {
          result.add(x);
          while (it1.hasNext()) {
            result.add(it1.next());
          }
          break;
        }
      }
    }
    return result;
  }

  public static void main(String[] args) {
    final List<Integer> randomList = Arrays.stream(
        ThreadLocalRandom.current().ints().limit(100000).toArray()).boxed().collect(Collectors.toList());

    ExecutorService executorService = Executors.newFixedThreadPool(4);

    executorService.submit(() -> {
      long startTime = System.currentTimeMillis();
      insertSort(randomList);
      System.out.println("insertion sort took: " + (System.currentTimeMillis() - startTime) + "ms");
    });

    executorService.submit(() -> {
      long startTime = System.currentTimeMillis();
      selectionSort(randomList);
      System.out.println("selection sort took: " + (System.currentTimeMillis() - startTime) + "ms");
    });

    executorService.submit(() -> {
      long startTime = System.currentTimeMillis();
      quickSort(randomList);
      System.out.println("quick sort took: " + (System.currentTimeMillis() - startTime) + "ms");
    });

    executorService.submit(() -> {
      long startTime = System.currentTimeMillis();
      mergeSort(randomList);
      System.out.println("merge sort took: " + (System.currentTimeMillis() - startTime) + "ms");
    });

    executorService.shutdown();
  }
}
