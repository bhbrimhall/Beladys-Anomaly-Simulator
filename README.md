# Belady's Anomaly Simulator
## What is this?
This is an assignment that I did for my CS3100 - Operating Systems and Concurrency class. It simulates page replacement in memory using three different algorithms: FIFO, LRU, and MRU. The process goes like:
1. Generate a page referece string
2. Assign each algorithm the sequence with a certain number of frames to use.
3. Record results
This program simulates 300,000 different page replacement scenarios using a threadpool to speed up computation time.
## Running the Simulation
If you want to use gradle to run the application:
1. Assemble the project with `$ gradle build`
2. Run the project with `gradle run`
If you don't want to use gradle:
1. Compile the .java files in the src directory
2. Execute `java Assign5.java` from the command line
