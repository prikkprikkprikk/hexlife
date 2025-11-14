public class Board implements HexLifeConstants {

    public Cell boardArray[][];
    public int sideLength;
    public int diameter;
    public String shape;
    private int top_row;
    private int middle_row;
    private int bottom_row;
    public int numberOfRows;
    public int generation;

    public Board(String requestedShape, int requestedSideLength) {
        shape = requestedShape;
        if (shape == HexLifeConstants.RECT)
            makeRectBoard(requestedSideLength);
        else if (shape == HexLifeConstants.HEX)
            makeHexBoard(requestedSideLength);
        findNeighbours();
        generation = 1;
    }

    public void advance() {
        for (int currentRow = 0; currentRow <= bottom_row; currentRow++) {
            for (int currentCol = 0; currentCol <= lastRowIndex(currentRow); currentCol++)
                boardArray[currentRow][currentCol].foretellFuture();

        }

        for (int currentRow = 0; currentRow <= bottom_row; currentRow++) {
            for (int currentCol = 0; currentCol <= lastRowIndex(currentRow); currentCol++)
                boardArray[currentRow][currentCol].realizeFuture();
        }

        generation++;
    }

    private void makeRectBoard(int requestedSideLength) {
        if (requestedSideLength < MINRECTSIZE)
            sideLength = MINRECTSIZE;
        else if (requestedSideLength > MAXRECTSIZE)
            sideLength = MAXRECTSIZE;
        else
            sideLength = requestedSideLength;
        bottom_row = (int) ((double) sideLength / HEX_HEIGHT_TO_WIDTH_RATIO);
        bottom_row = bottom_row - (bottom_row + 1) % 2;
        numberOfRows = bottom_row + 1;
        boardArray = new Cell[numberOfRows][];
        for (int currentRow = 0; currentRow <= bottom_row; currentRow++) {
            boardArray[currentRow] = new Cell[sideLength];
            for (int currentCol = 0; currentCol < sideLength; currentCol++) {
                int yPos = 320 - 8 * ((numberOfRows / 2 - currentRow) + 1);
                int xPos = 320 - 5 * (sideLength - currentCol * 2 - 1 - currentRow % 2);
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
        middle_row = sideLength - 1;
        bottom_row = diameter - 1;
        int currentRowLength = sideLength;
        int rowLengthChangeStep = 1;
        int horizontalOffset = sideLength;
        boardArray = new Cell[numberOfRows][];
        for (int currentRow = 0; currentRow < diameter; currentRow++) {
            boardArray[currentRow] = new Cell[currentRowLength];
            for (int currentCol = 0; currentCol < currentRowLength; currentCol++) {
                int yPos = 320 - 8 * ((sideLength - currentRow) + 1);
                int xPos = 320 - 5 * (diameter - horizontalOffset - currentCol * 2);
                boardArray[currentRow][currentCol] = new Cell(yPos, xPos);
            }

            if (currentRowLength == diameter)
                rowLengthChangeStep = -1;
            currentRowLength += rowLengthChangeStep;
            horizontalOffset -= rowLengthChangeStep;
        }

    }

    public void randomize() {
        for (int currentRow = 0; currentRow <= bottom_row; currentRow++) {
            int lastRowIndex = lastRowIndex(currentRow);
            for (int currentCol = 0; currentCol <= lastRowIndex; currentCol++)
                boardArray[currentRow][currentCol].randomize();
        }

        generation = 1;
    }

    private void findNeighbours() {
        for (int currentRow = 0; currentRow <= bottom_row; currentRow++) {
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
        if (shape == HexLifeConstants.HEX) {
            if (originRow == top_row) {
                targetRow = bottom_row;
                targetCol = originCol;
            } else if (originRow <= middle_row) {
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
            if (originRow == top_row) {
                targetRow = bottom_row;
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
        if (shape == HexLifeConstants.HEX) {
            if (originCol == lastRowIndex(originRow)) {
                if (originRow <= middle_row) {
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
        if (shape == HexLifeConstants.HEX) {
            if (originRow == bottom_row) {
                if (originCol == lastRowIndex(originRow)) {
                    targetRow = middle_row;
                    targetCol = 0;
                } else {
                    targetRow = top_row;
                    targetCol = originCol + 1;
                }
            } else if (originRow >= middle_row) {
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
            if (originRow == bottom_row)
                targetRow = top_row;
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
        if (shape == HexLifeConstants.HEX) {
            if (originRow == bottom_row) {
                targetRow = top_row;
                targetCol = originCol;
            } else if (originRow >= middle_row) {
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
            if (originRow == bottom_row)
                targetRow = top_row;
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
        if (shape == HexLifeConstants.HEX) {
            if (originCol == 0) {
                if (originRow < middle_row)
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
        if (shape == HexLifeConstants.HEX) {
            if (originRow == top_row) {
                if (originCol == 0) {
                    targetRow = middle_row;
                    targetCol = lastRowIndex(targetRow);
                } else {
                    targetRow = bottom_row;
                    targetCol = originCol - 1;
                }
            } else if (originRow <= middle_row) {
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
            if (originRow == top_row)
                targetRow = bottom_row;
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