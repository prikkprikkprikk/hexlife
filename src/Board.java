public class Board implements HexLifeConstants {

    public Cell boardArray[][];
    public int sideLength;
    public int diameter;
    public String shape;
    private int topRow = 0;
    private int middleRow;
    private int bottomRow;
    public int numberOfRows;
    public int generation;

    public Board(String requestedShape, int requestedSideLength) {
        PerformanceMonitor perf = PerformanceMonitor.getInstance();
        long initStartTime = System.nanoTime();

        shape = requestedShape;
        if (RECT.equals(shape))
            makeRectBoard(requestedSideLength);
        else if (HEX.equals(shape))
            makeHexBoard(requestedSideLength);
        findNeighbours();
        generation = 1;

        long initElapsedMs = (System.nanoTime() - initStartTime) / 1_000_000;
        perf.recordBoardInit(initElapsedMs);
    }

    public void advance() {
        PerformanceMonitor perf = PerformanceMonitor.getInstance();
        long generationStartTime = System.nanoTime();

        // Phase 1: Calculate future state for all cells
        for (int currentRow = 0; currentRow <= bottomRow; currentRow++) {
            for (int currentCol = 0; currentCol <= lastRowIndex(currentRow); currentCol++)
                boardArray[currentRow][currentCol].foretellFuture();
        }

        // Phase 2: Apply predicted state for all cells
        for (int currentRow = 0; currentRow <= bottomRow; currentRow++) {
            for (int currentCol = 0; currentCol <= lastRowIndex(currentRow); currentCol++)
                boardArray[currentRow][currentCol].realizeFuture();
        }

        generation++;

        // Record overall generation timing
        long elapsedMs = (System.nanoTime() - generationStartTime) / 1_000_000;
        perf.recordGeneration(elapsedMs);
    }

    private void makeRectBoard(int requestedSideLength) {
        if (requestedSideLength < MINRECTSIZE)
            sideLength = MINRECTSIZE;
        else if (requestedSideLength > MAXRECTSIZE)
            sideLength = MAXRECTSIZE;
        else
            sideLength = requestedSideLength;
        bottomRow = (int) ((double) sideLength / HEX_HEIGHT_TO_WIDTH_RATIO);
        bottomRow = bottomRow - (bottomRow + 1) % 2;
        numberOfRows = bottomRow + 1;
        boardArray = new Cell[numberOfRows][];
        for (int currentRow = 0; currentRow <= bottomRow; currentRow++) {
            boardArray[currentRow] = new Cell[sideLength];
            for (int currentCol = 0; currentCol < sideLength; currentCol++) {
                int yPos = SCREEN_CENTER_Y - GRIDSIZE_Y * ((numberOfRows / 2 - currentRow) + 1);
                int xPos = SCREEN_CENTER_X - GRIDSIZE_X * (sideLength - currentCol * 2 - 1 - currentRow % 2);
                boardArray[currentRow][currentCol] = new Cell(yPos, xPos);
            }
        }
    }

    private void makeHexBoard(int requestedSideLength) {
        if (requestedSideLength < MINHEXSIZE)
            sideLength = MINHEXSIZE;
        else if (requestedSideLength > MAXHEXSIZE)
            sideLength = MAXHEXSIZE;
        else
            sideLength = requestedSideLength;
        diameter = sideLength * 2 - 1;
        numberOfRows = diameter;
        middleRow = sideLength - 1;
        bottomRow = diameter - 1;
        int currentRowLength = sideLength;
        int rowLengthChangeStep = 1;
        int horizontalOffset = sideLength;
        boardArray = new Cell[numberOfRows][];
        for (int currentRow = 0; currentRow < diameter; currentRow++) {
            boardArray[currentRow] = new Cell[currentRowLength];
            for (int currentCol = 0; currentCol < currentRowLength; currentCol++) {
                int yPos = SCREEN_CENTER_Y - GRIDSIZE_Y * ((sideLength - currentRow) + 1);
                int xPos = SCREEN_CENTER_X - GRIDSIZE_X * (diameter - horizontalOffset - currentCol * 2);
                boardArray[currentRow][currentCol] = new Cell(yPos, xPos);
            }

            if (currentRowLength == diameter)
                rowLengthChangeStep = -1;
            currentRowLength += rowLengthChangeStep;
            horizontalOffset -= rowLengthChangeStep;
        }

    }

    public void randomize() {
        for (int currentRow = 0; currentRow <= bottomRow; currentRow++) {
            int lastRowIndex = lastRowIndex(currentRow);
            for (int currentCol = 0; currentCol <= lastRowIndex; currentCol++)
                boardArray[currentRow][currentCol].randomize();
        }

        generation = 1;
    }

    private void findNeighbours() {
        for (int currentRow = 0; currentRow <= bottomRow; currentRow++) {
            int lastRowIndex = lastRowIndex(currentRow);
            for (int currentCol = 0; currentCol <= lastRowIndex; currentCol++)
                boardArray[currentRow][currentCol].neighbours.setNeighbours(find_ne(currentRow, currentCol),
                        find_e(currentRow, currentCol), find_se(currentRow, currentCol),
                        find_sw(currentRow, currentCol),
                        find_w(currentRow, currentCol), find_nw(currentRow, currentCol));
        }
    }

    private int lastRowIndex(int row) {
        return boardArray[row].length - 1;
    }

    private Cell find_ne(int originRow, int originCol) {
        int targetRow;
        int targetCol;
        if (HEX.equals(shape)) {
            if (originRow == topRow) {
                targetRow = bottomRow;
                targetCol = originCol;
            } else if (originRow <= middleRow) {
                if (originCol == lastRowIndex(originRow)) {
                    targetRow = (originRow + sideLength) - 2;
                    targetCol = 0;
                } else {
                    targetRow = originRow - 1;
                    targetCol = originCol;
                }
            } else {
                targetRow = originRow - 1;
                targetCol = originCol + 1;
            }
        } else {
            if (originRow == topRow) {
                targetRow = bottomRow;
                targetCol = originCol;
            } else {
                targetRow = originRow - 1;
            }
            if (originCol == lastRowIndex(originRow)) {
                if (originRow % 2 == 1)
                    targetCol = 0;
                else
                    targetCol = originCol;
            } else {
                targetCol = originCol + originRow % 2;
            }
        }
        return boardArray[targetRow][targetCol];
    }

    private Cell find_e(int originRow, int originCol) {
        int targetRow;
        int targetCol;
        if (HEX.equals(shape)) {
            if (originCol == lastRowIndex(originRow)) {
                if (originRow <= middleRow) {
                    targetRow = (originRow + sideLength) - 1;
                    targetCol = 0;
                } else {
                    targetRow = originRow - sideLength;
                    targetCol = 0;
                }
            } else {
                targetRow = originRow;
                targetCol = originCol + 1;
            }
        } else {
            targetRow = originRow;
            if (originCol == lastRowIndex(originRow))
                targetCol = 0;
            else
                targetCol = originCol + 1;
        }
        return boardArray[targetRow][targetCol];
    }

    private Cell find_se(int originRow, int originCol) {
        int targetRow;
        int targetCol;
        if (HEX.equals(shape)) {
            if (originRow == bottomRow) {
                if (originCol == lastRowIndex(originRow)) {
                    targetRow = middleRow;
                    targetCol = 0;
                } else {
                    targetRow = topRow;
                    targetCol = originCol + 1;
                }
            } else if (originRow >= middleRow) {
                if (originCol == lastRowIndex(originRow)) {
                    targetRow = (originRow - sideLength) + 1;
                    targetCol = 0;
                } else {
                    targetRow = originRow + 1;
                    targetCol = originCol;
                }
            } else {
                targetRow = originRow + 1;
                targetCol = originCol + 1;
            }
        } else {
            if (originRow == bottomRow)
                targetRow = topRow;
            else
                targetRow = originRow + 1;
            if (originCol == lastRowIndex(originRow)) {
                if (originRow % 2 == 1)
                    targetCol = 0;
                else
                    targetCol = originCol;
            } else {
                targetCol = originCol + originRow % 2;
            }
        }
        return boardArray[targetRow][targetCol];
    }

    private Cell find_sw(int originRow, int originCol) {
        int targetRow;
        int targetCol;
        if (HEX.equals(shape)) {
            if (originRow == bottomRow) {
                targetRow = topRow;
                targetCol = originCol;
            } else if (originRow >= middleRow) {
                if (originCol == 0) {
                    targetRow = (originRow - sideLength) + 2;
                    targetCol = lastRowIndex(targetRow);
                } else {
                    targetRow = originRow + 1;
                    targetCol = originCol - 1;
                }
            } else {
                targetRow = originRow + 1;
                targetCol = originCol;
            }
        } else {
            if (originRow == bottomRow)
                targetRow = topRow;
            else
                targetRow = originRow + 1;
            if (originCol == 0) {
                if (originRow % 2 == 0)
                    targetCol = lastRowIndex(targetRow);
                else
                    targetCol = 0;
            } else {
                targetCol = (originCol - 1) + originRow % 2;
            }
        }
        return boardArray[targetRow][targetCol];
    }

    private Cell find_w(int originRow, int originCol) {
        int targetRow;
        int targetCol;
        if (HEX.equals(shape)) {
            if (originCol == 0) {
                if (originRow < middleRow)
                    targetRow = originRow + sideLength;
                else
                    targetRow = (originRow - sideLength) + 1;
                targetCol = lastRowIndex(targetRow);
            } else {
                targetRow = originRow;
                targetCol = originCol - 1;
            }
        } else {
            targetRow = originRow;
            if (originCol == 0)
                targetCol = lastRowIndex(originRow);
            else
                targetCol = originCol - 1;
        }
        return boardArray[targetRow][targetCol];
    }

    private Cell find_nw(int originRow, int originCol) {
        int targetRow;
        int targetCol;
        if (HEX.equals(shape)) {
            if (originRow == topRow) {
                if (originCol == 0) {
                    targetRow = middleRow;
                    targetCol = lastRowIndex(targetRow);
                } else {
                    targetRow = bottomRow;
                    targetCol = originCol - 1;
                }
            } else if (originRow <= middleRow) {
                if (originCol == 0) {
                    targetRow = (originRow + sideLength) - 1;
                    targetCol = lastRowIndex(targetRow);
                } else {
                    targetRow = originRow - 1;
                    targetCol = originCol - 1;
                }
            } else {
                targetRow = originRow - 1;
                targetCol = originCol;
            }
        } else {
            if (originRow == topRow)
                targetRow = bottomRow;
            else
                targetRow = originRow - 1;
            if (originCol == 0) {
                if (originRow % 2 == 0)
                    targetCol = lastRowIndex(targetRow);
                else
                    targetCol = 0;
            } else {
                targetCol = (originCol - 1) + originRow % 2;
            }
        }
        return boardArray[targetRow][targetCol];
    }
}