# Experiment 3 - CloudSim Shortest Job First scheduler

This project implements the Experiment 3 aim using CloudSim 3.0.3. It adds a custom **Shortest Job First (SJF)** policy by sorting cloudlets in ascending execution length before submitting them to one space-shared VM.

## Run

From this directory in PowerShell:

```powershell
javac -cp "lib\cloudsim-3.0.3.jar;lib\commons-math3-3.6.1.jar" -d out src\SjfScheduler.java
java -cp "out;lib\cloudsim-3.0.3.jar;lib\commons-math3-3.6.1.jar" SjfScheduler
```

Expected SJF submission order is job lengths: `1000, 2000, 3000, 4000, 5000`.
