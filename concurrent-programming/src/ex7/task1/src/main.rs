use std::fs::{metadata, read_dir};
use std::path::{Path, PathBuf};
use std::time::{Duration, Instant};
use std::env;

extern crate crossbeam;
extern crate num_cpus;
extern crate rayon;

fn get_size_of_files_in_dir_sequential(path: &Path) -> f64 {
  if path.is_file() {
    if let Ok(m) = metadata(path) {
      return m.len() as f64
    }
  }

  match read_dir(path) {
    Ok(entries) => entries.fold(0.0, |sum, entry|
      sum + get_size_of_files_in_dir_sequential(&entry.unwrap().path())),
    Err(_) => 0.0 //skip read only folders or else panic!("Could not read directory \"{}\" with error: {}", path.display(), e),
  }
}

fn get_size_of_files_in_dir_parallel(path: &Path) -> f64 {
  if let Ok(entries) = read_dir(path) {
    let paths: Vec<_> = entries.map(|entry| entry.unwrap().path()).collect();
    let num_tasks_per_thread = paths.len() / num_cpus::get();

    return crossbeam::scope(|scope| {
      let threads: Vec<_> = if num_tasks_per_thread > 0 {
        paths.chunks(num_tasks_per_thread).map(|chunk| scope.spawn(move ||
            chunk.iter().fold(0.0, |sum, val| sum + get_size_of_files_in_dir_sequential(&val)))).collect()
      } else {
        paths.iter().map(|p| scope.spawn(move || get_size_of_files_in_dir_sequential(&p))).collect()
      };

      return threads.into_iter().map(|t| t.join()).sum()
    });
  }

  panic!("Could not read directory \"{}\"", path.display())
}

fn get_size_of_files_in_dir_join(path: &Path, n_threads : usize) -> f64 {
  rayon::initialize(rayon::Configuration::new().num_threads(n_threads));

  fn join_rec(paths: &[PathBuf]) -> f64 {
    paths.iter().fold(0.0, |sum, path| {
      if path.is_file() {
          sum + metadata(path).unwrap().len() as f64
      } else {
        sum + match read_dir(path) {
          Ok(entries) => {
            let paths: Vec<_> = entries.map(|entry| entry.unwrap().path()).collect();
            let mid_point = paths.len() / 2;
            let (left, right) = paths.split_at(mid_point);
            let (a, b) = rayon::join(|| join_rec(left), || join_rec(right));
            a + b
          },
          Err(_) => 0.0
      }
    }})
  }

  join_rec(&[PathBuf::from(path)])
}

fn main() {
  let into_ms = |x: Duration| -> u64 {
    return (x.as_secs() * 1_000) + (x.subsec_nanos() / 1_000_000) as u64;
  };

  let args: Vec<String> = env::args().collect();

  if args.len() > 1 {
    let path = Path::new(&args[1]);
    let sequential_bench = Instant::now();
    println!("Running sequential operation for folder \"{}\"", &args[1]);
    println!("Total size of folder (sequential) {:.2} mb", get_size_of_files_in_dir_sequential(&path) / 1_048_576 as f64);
    let sequential_bench_elapsed = sequential_bench.elapsed();
    println!("sequential operation took {} ms", into_ms(sequential_bench_elapsed));

    let parallel_bench = Instant::now();
    println!("Running parallel operation for folder \"{}\"", &args[1]);
    println!("Total size of folder (parallel) {:.2} mb", get_size_of_files_in_dir_parallel(&path) / 1_048_576 as f64);
    let parallel_bench_elapsed = parallel_bench.elapsed();
    println!("parallel operation took {} ms", into_ms(parallel_bench_elapsed));

    let join_bench = Instant::now();
    println!("Running parallel join operation for folder \"{}\"", &args[1]);

    if args.len() > 2 {
      let result = match args[2].parse::<usize>() {
        Ok(n) => get_size_of_files_in_dir_join(&path, n) / 1_048_576 as f64,
        Err(_) => get_size_of_files_in_dir_join(&path, num_cpus::get()) / 1_048_576 as f64,
      };

      println!("Total size of folder (parallel join) {:.2} mb", result);
    } else {
      println!("Total size of folder (parallel join) {:.2} mb", get_size_of_files_in_dir_join(&path, num_cpus::get()) / 1_048_576 as f64);
    }

    let join_bench_elapsed = join_bench.elapsed();
    println!("parallel operation took {} ms", into_ms(join_bench_elapsed));
  } else {
    panic!("no command line argument was specified");
  }
}
