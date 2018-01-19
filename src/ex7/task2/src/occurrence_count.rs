use std::fs::File;
use std::io::BufReader;
use std::io::prelude::*;

use regex::Regex;

pub fn occurrence_count(re: &Regex, path: &String) -> usize {
  let file = match File::open(&path) {
    Ok(file) => file,
    Err(_) => panic!("Could not read '{}'!", path)
  };

  let mut buf_reader = BufReader::new(file);

  let mut content = String::new();

  buf_reader.read_to_string(&mut content).unwrap();

  re.find_iter(&content).count()
}
