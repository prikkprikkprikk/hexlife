# HexLife

A hexagonal version of Game of Life I made a few decades ago, back when Java could run as applets on the web.

![HexLife Screenshot](images/screenshot.png)

## Compile

```sh
javac -d bin src/*.java
```

## Run

### Normal Mode
```sh
./run.sh
```

The app launches in the background and the shell prompt returns immediately.

### Debug Mode (with Performance Monitoring)
```sh
./run.sh --debug
# or
./run.sh -d
```

Debug mode adds a **Report** button to the GUI that displays:
- Performance statistics (generations, average time per generation, estimated FPS)
- Memory usage (heap size, used memory, max heap)

### Manual Execution

To run without using the script:
```sh
javac -d bin src/*.java
cd bin && java HexLife        # Normal mode
cd bin && java HexLife --debug # Debug mode
```

## Performance Benchmarking

To run a comprehensive performance benchmark across all board sizes:
```sh
cd bin && java PerformanceBenchmark
```

This tests both hexagonal (sizes 10-100) and rectangular (sizes 30-150) boards, running 100 generations each and reporting:
- Cell count
- Total time and average time per generation
- Throughput (cells processed per millisecond)
- Estimated FPS
