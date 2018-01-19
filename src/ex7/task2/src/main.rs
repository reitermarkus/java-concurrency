use std::env;
use std::fs::{File, read_dir};
use std::io::BufReader;
use std::io::prelude::*;
use std::path::{Path, PathBuf};

extern crate crossbeam;
extern crate num_cpus;
extern crate rayon;
extern crate regex;

use regex::{escape, Regex, RegexBuilder};

fn word_occurrences(re: &Regex, path: &Path) -> usize {
  let file = match File::open(&path) {
    Ok(file) => file,
    Err(_) => panic!("Could not read '{}'!", path.to_str().unwrap())
  };

  let mut buf_reader = BufReader::new(file);

  let mut content = String::new();

  buf_reader.read_to_string(&mut content).unwrap();

  re.find_iter(&content).count()
}

fn word_occurrences_in_dir(word: &Regex, path: &Path, thread_count : usize) -> usize {
  rayon::initialize(rayon::Configuration::new().num_threads(thread_count)).unwrap();

  fn join_rec(word: &Regex, paths: &[PathBuf]) -> usize {
    paths.iter().fold(0, |sum, path| {
      if path.is_file() {
        sum + word_occurrences(&word, &path)
      } else {
        sum + match read_dir(path) {
          Ok(entries) => {
            let paths: Vec<_> = entries.map(|entry| entry.unwrap().path()).collect();
            let (left, right) = paths.split_at(paths.len() / 2);
            let (a, b) = rayon::join(|| join_rec(&word, left), || join_rec(&word, right));
            a + b
          },
          Err(_) => panic!("Could not read directory '{}'!", path.as_path().to_str().unwrap())
      }
    }})
  }

  join_rec(&word, &[PathBuf::from(path)])
}

fn main() {
  let mut args = env::args().skip(1);

  let dir = match args.next() {
    Some(dir) => {
      {
        let path = Path::new(&dir);
        if !path.is_dir() {
          panic!("'{}' is not a directory!", dir)
        }
      }
      dir
    },
    None => panic!("No directory given!")
  };

  let word = match args.next() {
    Some(word) => word,
    None => panic!("No word given!")
  };

  let threads = match args.next() {
    Some(threads) => threads.parse::<usize>().unwrap(),
    None => num_cpus::get()
  };

  println!("Searching for occurrences of '{}' inside '{}' …", word, dir);

  let re = RegexBuilder::new(&escape(&word))
             .multi_line(true)
             .case_insensitive(true)
             .build()
             .unwrap();

  let word_count = word_occurrences_in_dir(&re, &Path::new(&dir), threads);

  println!("{}", word_count);
}
