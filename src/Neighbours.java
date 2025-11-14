import java.awt.Color;

public class Neighbours implements HexLifeConstants {
    public Cell neighbours[];

    public Neighbours() {
        neighbours = new Cell[6];
    }

    public void setNeighbours(Cell cell, Cell cell1, Cell cell2, Cell cell3, Cell cell4, Cell cell5) {
        neighbours[0] = cell;
        neighbours[1] = cell1;
        neighbours[2] = cell2;
        neighbours[3] = cell3;
        neighbours[4] = cell4;
        neighbours[5] = cell5;
    }

    public int getNumberOfNeighbours() {
        int numberOfNeighbours = 0;
        for (int currentNeighbour = 0; currentNeighbour < 6; currentNeighbour++)
            if (neighbours[currentNeighbour].alive)
                numberOfNeighbours++;

        return numberOfNeighbours;
    }

    public Color getAverageColor() {
        int numberOfNeighbours = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        for (int currentNeighbour = 0; currentNeighbour < 6; currentNeighbour++)
            if (neighbours[currentNeighbour].alive) {
                numberOfNeighbours++;
                r += neighbours[currentNeighbour].cellColor.getRed();
                g += neighbours[currentNeighbour].cellColor.getGreen();
                b += neighbours[currentNeighbour].cellColor.getBlue();
            }

        if (numberOfNeighbours == 0)
            return HexLifeConstants.COLOR0;
        int rAvg = r / numberOfNeighbours;
        int gAvg = g / numberOfNeighbours;
        int bAvg = b / numberOfNeighbours;
        int rgbSum = r + g + b;
        int rgbAvgSumProduct = (rAvg + gAvg + bAvg) * numberOfNeighbours;
        int sumAvgDiff = rgbSum - rgbAvgSumProduct;
        if (sumAvgDiff != 0)
            switch ((int) Math.floor(Math.random() * 3D)) {
                case 0:
                    rAvg += sumAvgDiff;
                    break;

                case 1:
                    gAvg += sumAvgDiff;
                    break;

                case 2:
                    bAvg += sumAvgDiff;
                    break;
            }
        Color color = new Color(rAvg, gAvg, bAvg);
        return color;
    }
}