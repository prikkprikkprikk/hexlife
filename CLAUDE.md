# HexLife - Context for Future Work

## Project Overview
HexLife is a 25-year-old Java implementation of Conway's Game of Life on a hexagonal grid. Originally written as a Java applet (which is now obsolete), it has been converted to a modern Swing application.

## Architecture

### Core Classes
- **Board.java**: Manages the game state and logic
  - Supports two grid shapes: rectangular and hexagonal
  - Implements neighbor-finding for both grid topologies with wrapping edges
  - The `find_ne()`, `find_e()`, etc. methods contain complex but clever geometry logic

- **Cell.java**: Individual cell state (alive/dead)
  - Implements Conway's Game of Life rules
  - Manages cell color based on neighbor averaging

- **Neighbours.java**: Manages the 6 neighbor relationships for hexagonal cells
  - `getNumberOfNeighbours()`: Counts alive neighbors
  - `getAverageColor()`: Averages colors of alive neighbors with rounding error distribution

- **HexLife.java**: Swing GUI (converted from Applet)
  - Window-based application (no longer browser-based)
  - Controls: Animate, Step, Randomize buttons; board size and shape selection
  - Thread-based animation with stop mechanism

- **HexLifeConstants.java**: Configuration constants including grid sizes and screen coordinates

## Known Issues & Notes

### Board Size Limits
- Rectangular boards: min 10, max 150
- Hexagonal boards: min 7, max 100
- These limits are defined in HexLifeConstants and enforced in Board constructor
- The neighbor-finding logic in the `find_*` methods depends on these constraints

### Complex Geometry
The hexagonal grid wrapping logic in `find_ne()` through `find_nw()` is intentionally complex:
- Handles edge cases for both HEX and RECT grid shapes
- Implements toroidal wrapping (edges wrap around)
- Different behavior for upper/lower halves of hexagonal boards due to row width changes
- This logic is correct and shouldn't be "optimized" - it's as readable as it can be for this complexity

## How to Run
See README.md for current usage instructions (launch modes, debug flags, performance benchmarking).

Quick reference:
```bash
./run.sh              # Normal mode (launches in background)
./run.sh --debug      # Debug mode with performance monitoring
cd bin && java PerformanceBenchmark  # Run performance tests
```

## Important: Updating Documentation
**When adding features that affect how users run the app**, always update README.md:
- New command-line arguments or flags
- New execution modes or options
- New ways to run the application
- Performance testing instructions
- Debug/development tools accessible to users

This keeps the README as the single source of truth for users, while CLAUDE.md remains for internal architecture and development notes.

## Git Repository
User has full control over the git repository. Do not suggest or execute git commands. The user will handle all git operations (commit, push, branching) themselves.

## Testing
No unit tests exist. Candidates for testing:
- Board evolution logic (verify known patterns like gliders)
- Neighbor counting for edge cases
- Shape conversion between hex/rect modes
- Grid wrapping at boundaries

## Refactoring Strategy

### Overview
To practice test-driven refactoring while preserving the proven working logic, use an **API wrapper approach**:

1. **Create a new clean API layer** (e.g., `GameSimulation` class) that wraps the existing Board/Cell/Neighbours implementation
2. **Write tests against the new API** - Tests verify behavior without coupling to the old implementation details
3. **Incrementally refactor internals** - Replace old code one piece at a time while the wrapper API stays stable and tests stay green
4. **Update GUI to use the wrapper** - HexLife.java calls the new API instead of Board directly

### Why This Approach

- **Preserves working code** - Current logic is proven; keep it initially
- **Gets testable seams** - New API provides a clean testing surface
- **True refactoring** - Practice refactoring *with* tests as a constraint
- **Low risk** - Can replace old code gradually; tests catch regressions immediately

### Implementation Steps

**Phase 1: Create the wrapper**
- Create `GameSimulation` class with core methods:
  - `step()` - advance one generation
  - `randomize(int seed)` - populate with random cells
  - `getCell(int row, int col)` - check if cell is alive
  - `setCellState(int row, int col, boolean alive)` - set a cell
  - `getBoardSize()` - return current board dimensions
  - `getGridShape()` - return current grid type (HEX/RECT)
  - `setGridSize(int size, GridShape shape)` - change board configuration

**Phase 2: Write tests**
- Test `step()` with known patterns (blinkers, blocks, etc.)
- Test edge cases (board wrapping, boundaries)
- Test state persistence and transitions
- Test initialization and randomization

**Phase 3: Refactor incrementally**
- Update HexLife.java to call GameSimulation instead of Board
- Refactor Board, Cell, and Neighbours one at a time
- Keep the wrapper API stable; tests ensure nothing breaks

### Notes
- The old Board/Cell/Neighbours implementation is complex but correct—don't "fix" it until you have test coverage
- Start with simple test cases, then expand to edge cases and known Game of Life patterns
- The wrapper allows you to change implementation details without changing test code

## Future Improvements
1. Add unit tests for board evolution
2. Consider using 2D coordinate system (Point class) instead of raw row/col integers
3. Modernize from AWT to pure Swing (Button → JButton, Checkbox → JCheckBox, etc.)
4. Consider strategy pattern for geometry: extract HexGeometry and RectGeometry classes
5. DRY nested loops: extract common row/col iteration pattern with BiConsumer or Stream API
