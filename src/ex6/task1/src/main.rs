mod number_range;

use number_range::NumberRange;

extern crate crossbeam;

fn main() {
  let lower = 10;
  let upper = 20;

  let range = NumberRange::new(lower, upper);

  println!("Is 11 in Range? {}", range.is_in_range(11));

  range.set_lower(13);

  println!("Is 11 still in Range? {}", range.is_in_range(11));

  range.set_lower(10);

  println!("Is 11 in Range again? {}", range.is_in_range(11));

  range.set_upper(10);

  println!("Is 11 still in Range? {}", range.is_in_range(11));

}
