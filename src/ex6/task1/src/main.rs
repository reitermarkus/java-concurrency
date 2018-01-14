mod number_range;

use number_range::NumberRange;

use rand::Rng;
use std::sync::{Arc};

extern crate crossbeam;
extern crate rand;

fn main() {
  let range = Arc::new(NumberRange::new(0, 0));

  let thread_count = 4;

  crossbeam::scope(|scope| {
    let threads: Vec<_> = (0..thread_count).into_iter().map(|_| {
      scope.spawn(|| {
        let mut rng = rand::thread_rng();

        loop {
          let lower: i64 = rng.gen();
          let upper: i64 = rng.gen();

          range.set_lower(lower);
          range.set_upper(upper);
        }
      })
    }).collect();

    let printer_thread = scope.spawn(|| {
      let mut rng = rand::thread_rng();

      loop {
        let num: i64 = rng.gen();

        println!("Is {} in range? {}", &num, &range.is_in_range(num));
      }
    });

    threads.into_iter().for_each(|t| t.join());
    printer_thread.join();
  });
}
