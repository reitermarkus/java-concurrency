use std::fs::{metadata, read_dir};
use std::path::Path;
use std::time::{Duration, Instant};
use std::env;

extern crate crossbeam;
extern crate num_cpus;

fn get_size_of_files_in_dir_sequential(path: &Path) -> f64 {
  if path.is_file() {
    if let Ok(m) = metadata(path) {
      return m.len() as f64
    }
  }

  match read_dir(path) {
    Ok(entries) => entries.fold(0.0, |sum, entry|
      sum + get_size_of_files_in_dir_sequential(&entry.unwrap().path())),
    Err(e) => panic!("Could not read directory \"{}\" with error: {}", path.display(), e),
  }
}

fn get_size_of_files_in_dir_parallel(path: &Path) -> f64 {
  if let Ok(entries) = read_dir(path) {
    let paths: Vec<_> = entries.map(|entry| entry.unwrap().path()).collect();
    let num_tasks_per_thread = paths.len() / num_cpus::get();

    return crossbeam::scope(|scope| {
      let threads: Vec<_> = paths
        .chunks(num_tasks_per_thread).map(|chunk| scope.spawn(move ||
          chunk.iter().fold(0.0, |sum, val| sum + get_size_of_files_in_dir_sequential(&val))
      )).collect();

      return threads.into_iter().map(|t| t.join()).sum()
    });
  }

  panic!("Could not read directory \"{}\"", path.display())
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
  } else {
    panic!("no command line argument was specified");
  }
}
