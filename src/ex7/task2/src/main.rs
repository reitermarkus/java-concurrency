use std::env;
use std::path::Path;

mod occurrence_count;

use occurrence_count::occurrence_count;

extern crate crossbeam;
extern crate num_cpus;
extern crate rayon;
extern crate regex;

use regex::RegexBuilder;

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

  println!("Searching for '{}' in '{}' …", word, dir);

  let re = RegexBuilder::new(&word)
             .multi_line(true)
             .case_insensitive(true)
             .build()
             .unwrap();

             println!("{:?}", re);

  let word_count = occurrence_count(&re, &String::from("dir/document100.txt"));

  println!("{}", word_count);
}
