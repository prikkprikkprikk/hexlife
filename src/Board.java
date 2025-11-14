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
        for (int j = 0; j <= bottom_row; j++) {
            int i = lastRowIndex(j);
            for (int k = 0; k <= i; k++)
                boardArray[j][k].randomize();
        }

        generation = 1;
    }

    private void findNeighbours() {
        for (int i = 0; i <= bottom_row; i++) {
            int j = lastRowIndex(i);
            for (int k = 0; k <= j; k++)
                boardArray[i][k].neighbours.setNeighbours(find_ne(i, k), find_e(i, k), find_se(i, k), find_sw(i, k),
                        find_w(i, k), find_nw(i, k));

        }

    }

    private int lastRowIndex(int i) {
        return boardArray[i].length - 1;
    }

    private Cell find_ne(int i, int j) {
        int k;
        int l;
        if (shape == HexLifeConstants.HEX) {
            if (i == top_row) {
                k = bottom_row;
                l = j;
            } else if (i <= middle_row) {
                if (j == lastRowIndex(i)) {
                    k = (i + sideLength) - 2;
                    l = 0;
                } else {
                    k = i - 1;
                    l = j;
                }
            } else {
                k = i - 1;
                l = j + 1;
            }
        } else {
            if (i == top_row) {
                k = bottom_row;
                l = j;
            } else {
                k = i - 1;
            }
            if (j == lastRowIndex(i)) {
                if (i % 2 == 1)
                    l = 0;
                else
                    l = j;
            } else {
                l = j + i % 2;
            }
        }
        return boardArray[k][l];
    }

    private Cell find_e(int i, int j) {
        int k;
        int l;
        if (shape == HexLifeConstants.HEX) {
            if (j == lastRowIndex(i)) {
                if (i <= middle_row) {
                    k = (i + sideLength) - 1;
                    l = 0;
                } else {
                    k = i - sideLength;
                    l = 0;
                }
            } else {
                k = i;
                l = j + 1;
            }
        } else {
            k = i;
            if (j == lastRowIndex(i))
                l = 0;
            else
                l = j + 1;
        }
        return boardArray[k][l];
    }

    private Cell find_se(int i, int j) {
        int k;
        int l;
        if (shape == HexLifeConstants.HEX) {
            if (i == bottom_row) {
                if (j == lastRowIndex(i)) {
                    k = middle_row;
                    l = 0;
                } else {
                    k = top_row;
                    l = j + 1;
                }
            } else if (i >= middle_row) {
                if (j == lastRowIndex(i)) {
                    k = (i - sideLength) + 1;
                    l = 0;
                } else {
                    k = i + 1;
                    l = j;
                }
            } else {
                k = i + 1;
                l = j + 1;
            }
        } else {
            if (i == bottom_row)
                k = top_row;
            else
                k = i + 1;
            if (j == lastRowIndex(i)) {
                if (i % 2 == 1)
                    l = 0;
                else
                    l = j;
            } else {
                l = j + i % 2;
            }
        }
        return boardArray[k][l];
    }

    private Cell find_sw(int i, int j) {
        int k;
        int l;
        if (shape == HexLifeConstants.HEX) {
            if (i == bottom_row) {
                k = top_row;
                l = j;
            } else if (i >= middle_row) {
                if (j == 0) {
                    k = (i - sideLength) + 2;
                    l = lastRowIndex(k);
                } else {
                    k = i + 1;
                    l = j - 1;
                }
            } else {
                k = i + 1;
                l = j;
            }
        } else {
            if (i == bottom_row)
                k = top_row;
            else
                k = i + 1;
            if (j == 0) {
                if (i % 2 == 0)
                    l = lastRowIndex(k);
                else
                    l = 0;
            } else {
                l = (j - 1) + i % 2;
            }
        }
        return boardArray[k][l];
    }

    private Cell find_w(int i, int j) {
        int k;
        int l;
        if (shape == HexLifeConstants.HEX) {
            if (j == 0) {
                if (i < middle_row)
                    k = i + sideLength;
                else
                    k = (i - sideLength) + 1;
                l = lastRowIndex(k);
            } else {
                k = i;
                l = j - 1;
            }
        } else {
            k = i;
            if (j == 0)
                l = lastRowIndex(i);
            else
                l = j - 1;
        }
        return boardArray[k][l];
    }

    private Cell find_nw(int i, int j) {
        int k;
        int l;
        if (shape == HexLifeConstants.HEX) {
            if (i == top_row) {
                if (j == 0) {
                    k = middle_row;
                    l = lastRowIndex(k);
                } else {
                    k = bottom_row;
                    l = j - 1;
                }
            } else if (i <= middle_row) {
                if (j == 0) {
                    k = (i + sideLength) - 1;
                    l = lastRowIndex(k);
                } else {
                    k = i - 1;
                    l = j - 1;
                }
            } else {
                k = i - 1;
                l = j;
            }
        } else {
            if (i == top_row)
                k = bottom_row;
            else
                k = i - 1;
            if (j == 0) {
                if (i % 2 == 0)
                    l = lastRowIndex(k);
                else
                    l = 0;
            } else {
                l = (j - 1) + i % 2;
            }
        }
        return boardArray[k][l];
    }
}