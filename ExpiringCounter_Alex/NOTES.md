## [DEVELOPER NOTES] - This is very-very interesting problem covering wide range of design decisions and trade-offs. I really would like to start work in company that dealing with such tasks on daily basis and start delivering value.


### A. Key Design Explanation
- I chose data structure of buckets due to uncertainty of when record() will be called - so we are not creating new buckets constantly, but only on demand.
- The data structure inside bucket is array to allow simple computation of counters with O(1) random access to elements.
- This combination yields benefits of both coarse and fine granularity of data.
- Expiration / cleanup strategy - I selected combination of lazy(on getCount) and eager(on record) cleanup to improve performance of both methods . Background cleanup would also improve, but was not implemented due to time constraints.
- Thread-safety strategy - using synchronized(this) - since it is efficient enough for the exercise and does not involve more complicated logic of maintaining locks. For production-like use case such design would be definitely reconsidered for all combinations of threads/clock safe mutations.
- The time complexity of record() is O(1) because it will perform simple increment with instant access. Only on boundaries of new bucket it will spend more time to add new bucket with allocation of array of size 1000. 
- Also, when expiration detected, it will add some extra time to remove expired bucket and recalculate total counter, but it will be amortized O(1) for most use cases.
- Space complexity of record() is O(1) for most calls, but on boundaries of new bucket it will be O(1000) due to allocation of new array.
- The time and space complexity of record() and getCount()

### B. Testing Strategy
- I prioritized test testRollingWindow because it discovered incomplete initial implementation that was lack of granular expiration inside bucket
- Important edge cases not covered : test with very high load with combination of clock advancing and multiple record and getCount calls.

### C. Design Evolution
- The initial expiration was not worked as expected.
- During implementation of expiration I have added more granular expiration inside bucket, because I found out that it is not enough to remove whole bucket, but also need to remove expired records inside bucket.

### D. Known Limitations
- What I would improve with more time:
  - Add background cleanup that will be triggered by clock and will remove expired buckets and records, so we will have less data to process on both methods.
  - Add ability to select different expiration strategies (lazy, eager, background) based on use case and load.
  - Add metrics to measure performance and resource usage of the counter, so we can make informed decisions about optimization and scaling.
  - Debug the tests on critical flows to verify decisions on boundaries are correct.
  - Run the tests with IntelliJ Profiler to identify weaknesses and optimize performance.
- Remaining uncertainties:
  - The problem statement requires to provide a precise boundary but is not defining it explicitly.
  - We are not checking or limiting clock advancement, so at some moment it can overflow and cause unexpected behavior.
  - We are not limiting thread count, records count and amount of API calls and windowsMillis size, so the application may run out of memory or have performance issues in some extreme cases.
  - Incorrect locking mechanism implementation may cause deadlocks or threads starvation.

### E. Optional Reflection on AI
- AI helped to understand the problem and requirements in better way
- At the beginning of the exercise I have prompted different AI tools to get some different perspectives on the problem
- I have prompted : ChatGPT, Claude Opus 4.1, Gemini, and Perplexity.
- I selected ChatGPT for the implementation and tests, because I find it more convenient for brainstorming like with companion.
- AI struggled to provide implementation suggestion due to first prompt restriction that instructed do not expose design or classes implementation details
- What I would do differently : I would put more effort to implement test with more coverage of very high load with combination of clock advancing and multiple record and getCount calls. 