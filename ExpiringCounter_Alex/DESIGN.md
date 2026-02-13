## Problem Overview
Implement an ExpiringCounter in Java.
It supports:
- record(): record an event at the current time
- getCount(): return the number of events recorded in the last windowMillis milliseconds

### A. API Design
- I will implement the `ExpiringCounter` class with the following methods:
  - `record()`: This method will record an event at the current time.
  - `long getCount()`: This method will return the number of events(long) recorded in the last `windowMillis` milliseconds.
  - CTOR `ExpiringCounter(long windowMillis, Clock clock)`: This constructor will create the counter with a specified time window and a clock instance.
  - Those APIs are appropriate because they provide the required functionality while allowing usage of different clock implementations for testing.
- I will implement the `TestClock` class with the following methods:
  - `long now()`: This method will return the current time in milliseconds.
  - `void advance(long millis)`: This method will advance the current time by the specified number of milliseconds.
  - `void tick()`: This method will advance the current time by 1 millisecond.
  - CTOR `TestClock()`: This constructor will create the clock with a default start time 0.
  - CTOR `TestClock(Long now)`: This constructor will create the clock with provided time now.
  - Those APIs are appropriate because they allow to use and control functionality of the clock from the test suites.
- I will implement the `ExpiringCounterTest` class with the following methods:
  - `testEmptyCounter()`: This test will verify that a new counter returns 0.
  - `testSimpleRecording()`: This test will verify that single-threaded recording events increases the count correctly.
  - `testExpiration()`: This test will verify that events expire after the specified time window.
  - `testRollingWindow()`: This test will verify that the counter correctly counts events in a rolling time window.
  - `testBoundaries()`: This test will verify that events recorded exactly at the boundary of the time window are handled correctly.
  - `testInvalidInput()`: This test will verify that the counter handles invalid input (for example negative windowMillis) appropriately.
  - `testConcurrentRecordingAndCounting()`: This test will verify that the counter is thread-safe when few threads calling both methods.
  - Those APIs are appropriate because they cover all the required functionality and edge cases.

### B. Time Abstraction
- Class `TestClock` implements the `Clock` interface.
- Tests and production will have the hook allowing to advance and get the time by provided method now()
- Real-time call is prohibited because tests have no control on system clock and this may cause non-deterministic behavior on boundaries.

### C. Data Structure Choice
- I will store buckets that are bounded by 1 second, and each bucket will store the count of events that occurred in that second.
- I will use Deque backed by double-linked list to store the buckets
- Each bucket will be backed by an Array of size 1000, where each index will represent the count of events that occurred in that millisecond.
- This data structure allows efficient addition and removal of records, since access to the Head and to the Tail of the Deque is O(1). 
Counter of each bucket will be also persisted, allowing quick calculation of total counter, because at any moment we will have only one active changing bucket adding records and only one bucket that removing records.
- Inside single bucket we need random access to the element by millisecond, so Array is the best choice, because it provides O(1) access time compared to another data structures(for example LinkedList) that may reach O(n).
- Complexity of record() method will be O(1) because we will always add record to the Head bucket and will have instant access to the correct element just by the timestamp of millisecond.
- Complexity of getCount() method will be O(1) in best case because we will have the total counter of all buckets, and O(n) in worst case when we will have to remove all expired buckets and recalculate the total counter.
- Complexity of getCount() method for amortized case will be something between O(1) and O(n) depending on the frequency of events and the size of the time window, but it will be efficient for most use cases where events are not extremely frequent or the time window is not excessively large.
- Also, it will depend on expiration strategies as will be described in the next section.

### D. Expiration Strategy
- The minimal approach to handle expiration is to check for expired buckets only on count. But then all the records added on record will introduce extra complexity recalculating and checking for expired records.
- Adding expiration on record will improve the performance of getCount() since we will remove some records/bucket that already expired.
- If we have running application that already filled many buckets with many data, and then we have some quiet period with no records and getCount() calls, we can use background expiration(by the clock, not extra scheduled task) that will release resources and decrease time consumption of all following calls.
- Using all three strategies will improve amortized complexity and yield benefit since each call will hold lock less time, because it will have less data to process.
- Removal will acquire lock and decide which buckets are expired, as well as single elements inside one bucket.
- Eager cleanup will improve getCounter() calls because we will have less data to process, comparing to lazy cleanup that will hold lock for more time and will introduce overhead of both removing the expired records and counting the relevant records.
- Given test scenario that few threads calling both methods, this choice fit the design because both record and getCount will be less time consuming and more efficient.

### E. Thread-Safety Strategy
- I will use ReentrantLock to ensure thread safety of the counter, while work will be performed only on relatively small relevant subset of data.

### F. Complexity Analysis
- Complexity of record() method will be O(1) because we will always add record to the Head bucket and will have instant access to the correct element just by the timestamp of millisecond.
- Complexity of getCount() method will be O(1) in best case because we will have the total counter of all buckets, and O(n) in worst case when we will have to remove all expired buckets and recalculate the total counter.
- Complexity of getCount() method for amortized case will be something between O(1) and O(n) depending on the frequency of events and the size of the time window, but it will be efficient for most use cases where events are not extremely frequent or the time window is not excessively large.
- "Amortized" performance in this design is achieved by the fact that once some bucket expired , we can remove it without recalculating single elements.

### G. Boundary Semantics
- If the record timestamp is exactly on the boundary of the time window (means (now - timestamp)<=windowMillis), it will be included in the count. 
- For example, if the time window is 1000 milliseconds and an event is recorded at time T, then it will be counted in getCount() calls until time T + 1000 milliseconds including. 
- After that, it will expire and will not be included in the count anymore.

### H. Uncertanties
- The problem statement requires to provide a precise boundary but is not defining it explicitly.
- We are not checking or limiting clock advancement, so at some moment it can overflow and cause unexpected behavior.
- We are not limiting thread count, records count and amount of API calls and windowsMillis size, so the application may run out of memory or have performance issues in some extreme cases.
- Incorrect locking mechanism implementation may cause deadlocks or threads starvation.