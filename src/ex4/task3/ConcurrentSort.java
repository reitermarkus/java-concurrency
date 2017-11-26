package ex4.task3;

import helper.Benchmark;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by Michael Kaltschmid on 25.11.2017.
 */
public class ConcurrentSort {
  public static <T extends Comparable<? super T>> List<T> insertionSort(List<T> list) {
    for (int i = 1; i < list.size(); i++) {
      T value = list.get(i);
      int j = i - 1;

      while (j >= 0 && list.get(j).compareTo(value) < 0) {
        list.set(j + 1, list.get(j));
        j = j - 1;
      }

      list.set(j + 1, value);
    }

    return list;
  }

  public static <T extends Comparable<? super T>> List<T> selectionSort(List<T> list) {
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

    return list;
  }

  public static <T extends Comparable<? super T>> List<T> bubbleSort(List<T> list) {
    boolean changed;
    do {
      changed = false;
      for (int a = 0; a < list.size() - 1; a++) {
        if (list.get(a).compareTo(list.get(a + 1)) > 0) {
          T swap = list.get(a);
          list.set(a, list.get(a + 1));
          list.set(a + 1, swap);
          changed = true;
        }
      }
    } while (changed);

    return list;
  }

  public static <T extends Comparable<? super T>> List<T> quickSort(List<T> list) {
    if (!list.isEmpty()) {
      T pivot = list.get(0);

      List<T> less = new LinkedList<>();
      List<T> pivotList = new LinkedList<>();
      List<T> more = new LinkedList<>();

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

    return merge(left, right);
  }

  public static <T extends Comparable<? super T>> List<T> merge(List<T> left, List<T> right) {
    List<T> result = new ArrayList<>();
    Iterator<T> itLeft = left.iterator();
    Iterator<T> itRight = right.iterator();

    T x = itLeft.next();
    T y = itRight.next();

    while (true) {
      if (x.compareTo(y) <= 0) {
        result.add(x);
        if (itLeft.hasNext()) {
          x = itLeft.next();
        } else {
          result.add(y);
          while (itRight.hasNext()) {
            result.add(itRight.next());
          }

          break;
        }
      } else {
        result.add(y);
        if (itRight.hasNext()) {
          y = itRight.next();
        } else {
          result.add(x);
          while (itLeft.hasNext()) {
            result.add(itLeft.next());
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

    ExecutorService executorService = Executors.newFixedThreadPool(5);
    final CyclicBarrier cyclicBarrier = new CyclicBarrier(5);

    Runnable setCyclicBarrier = () -> {
      try {
        cyclicBarrier.await();
      } catch (InterruptedException | BrokenBarrierException e) {
        e.printStackTrace();
      }
    };

    executorService.submit(() -> {
      setCyclicBarrier.run();

      Benchmark.measure("insertion sort", () -> {
        insertionSort(randomList);
      });
    });

    executorService.submit(() -> {
      setCyclicBarrier.run();

      Benchmark.measure("selection sort", () -> {
        selectionSort(randomList);
      });
    });

    executorService.submit(() -> {
      setCyclicBarrier.run();

      Benchmark.measure("quick sort", () -> {
        quickSort(randomList);
      });
    });

    executorService.submit(() -> {
      setCyclicBarrier.run();

      Benchmark.measure("merge sort", () -> {
        mergeSort(randomList);
      });
    });

    executorService.submit(() -> {
      setCyclicBarrier.run();

      Benchmark.measure("bubble sort", () -> {
        bubbleSort(randomList);
      });
    });

    executorService.shutdown();
  }
}
