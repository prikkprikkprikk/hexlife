public class Board
        implements HexLifeConstants {

    public Board(String s, int i) {
        shape = s;
        if (shape == HexLifeConstants.RECT)
            makeRectBoard(i);
        else if (shape == HexLifeConstants.HEX)
            makeHexBoard(i);
        findNeighbours();
        generation = 1;
    }

    public void advance() {
        for (int i = 0; i <= bottom_row; i++) {
            for (int j = 0; j <= e_end(i); j++)
                boardArray[i][j].foretellFuture();

        }

        for (int k = 0; k <= bottom_row; k++) {
            for (int l = 0; l <= e_end(k); l++)
                boardArray[k][l].realizeFuture();

        }

        generation++;
    }

    private void makeRectBoard(int i) {
        if (i < 10)
            side = 10;
        else if (i > 60)
            side = 60;
        else
            side = i;
        bottom_row = (int) ((double) side / 0.86599999999999999D);
        bottom_row = bottom_row - (bottom_row + 1) % 2;
        cellCount = side ^ bottom_row + 1;
        numberOfRows = bottom_row + 1;
        boardArray = new Cell[bottom_row + 1][];
        for (int l = 0; l <= bottom_row; l++) {
            boardArray[l] = new Cell[side];
            for (int i1 = 0; i1 < side; i1++) {
                int j = 320 - 8 * ((numberOfRows / 2 - l) + 1);
                int k = 320 - 5 * (side - i1 * 2 - 1 - l % 2);
                boardArray[l][i1] = new Cell(j, k);
            }

        }

    }

    private void makeHexBoard(int i) {
        if (i < 7)
            side = 7;
        else if (i > 30)
            side = 30;
        else
            side = i;
        diameter = side * 2 - 1;
        numberOfRows = diameter;
        cellCount = side * (side - 1) * 3 - 1;
        middle_row = side - 1;
        bottom_row = diameter - 1;
        int j = side;
        byte byte0 = 1;
        int i1 = side;
        boardArray = new Cell[numberOfRows][];
        for (int j1 = 0; j1 < diameter; j1++) {
            boardArray[j1] = new Cell[j];
            for (int k1 = 0; k1 < j; k1++) {
                int k = 320 - 8 * ((side - j1) + 1);
                int l = 320 - 5 * (diameter - i1 - k1 * 2);
                boardArray[j1][k1] = new Cell(k, l);
            }

            if (j == diameter)
                byte0 = -1;
            j += byte0;
            i1 -= byte0;
        }

    }

    public void randomize() {
        for (int j = 0; j <= bottom_row; j++) {
            int i = e_end(j);
            for (int k = 0; k <= i; k++)
                boardArray[j][k].randomize();

        }

        generation = 1;
    }

    private void findNeighbours() {
        for (int i = 0; i <= bottom_row; i++) {
            int j = e_end(i);
            for (int k = 0; k <= j; k++)
                boardArray[i][k].neighbours.setNeighbours(find_ne(i, k), find_e(i, k), find_se(i, k), find_sw(i, k),
                        find_w(i, k), find_nw(i, k));

        }

    }

    private int e_end(int i) {
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
                if (j == e_end(i)) {
                    k = (i + side) - 2;
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
            if (j == e_end(i)) {
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
            if (j == e_end(i)) {
                if (i <= middle_row) {
                    k = (i + side) - 1;
                    l = 0;
                } else {
                    k = i - side;
                    l = 0;
                }
            } else {
                k = i;
                l = j + 1;
            }
        } else {
            k = i;
            if (j == e_end(i))
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
                if (j == e_end(i)) {
                    k = middle_row;
                    l = 0;
                } else {
                    k = top_row;
                    l = j + 1;
                }
            } else if (i >= middle_row) {
                if (j == e_end(i)) {
                    k = (i - side) + 1;
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
            if (j == e_end(i)) {
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
                    k = (i - side) + 2;
                    l = e_end(k);
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
                    l = e_end(k);
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
                    k = i + side;
                else
                    k = (i - side) + 1;
                l = e_end(k);
            } else {
                k = i;
                l = j - 1;
            }
        } else {
            k = i;
            if (j == 0)
                l = e_end(i);
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
                    l = e_end(k);
                } else {
                    k = bottom_row;
                    l = j - 1;
                }
            } else if (i <= middle_row) {
                if (j == 0) {
                    k = (i + side) - 1;
                    l = e_end(k);
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
                    l = e_end(k);
                else
                    l = 0;
            } else {
                l = (j - 1) + i % 2;
            }
        }
        return boardArray[k][l];
    }

    public Cell boardArray[][];
    public int side;
    public int cellCount;
    public int diameter;
    public String shape;
    private int top_row;
    private int middle_row;
    private int bottom_row;
    public int numberOfRows;
    public int generation;
}