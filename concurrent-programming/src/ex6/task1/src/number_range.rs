use crossbeam::epoch::{self, Atomic, Owned};
use std::sync::atomic::Ordering::{Acquire, Release};

pub struct NumberRange {
  lower: Atomic<i64>,
  upper: Atomic<i64>,
}

impl NumberRange {
  pub fn new(lower: i64, upper: i64) -> NumberRange {
    NumberRange { lower: Atomic::new(lower), upper: Atomic::new(upper) }
  }

  pub fn set_lower(&self, v: i64) {
    let guard = epoch::pin();

    if v > **self.upper.load(Acquire, &guard).unwrap() {
      panic!("can't set lower to {} > upper", &v)
    }

    self.lower.store(Some(Owned::new(v)), Release);
  }

  pub fn set_upper(&self, v: i64) {
    let guard = epoch::pin();

    if v < **self.lower.load(Acquire, &guard).unwrap() {
      panic!("can't set upper to {} < lower", &v)
    }

    self.upper.store(Some(Owned::new(v)), Release);
  }

  pub fn is_in_range(&self, v: i64) -> bool {
    let guard = epoch::pin();

    return v >= **self.lower.load(Acquire, &guard).unwrap() && v <= **self.upper.load(Acquire, &guard).unwrap()
  }
}

unsafe impl Sync for NumberRange {}
unsafe impl Send for NumberRange {}
