import java.awt.Color;

public class Cell implements HexLifeConstants {

    public boolean alive;
    public Color cellColor;
    public int screenX;
    public int screenY;
    public Neighbours neighbours;
    public boolean futureLife;
    public Color futureColor;

    public Cell(int yPos, int xPos) {
        neighbours = new Neighbours();
        screenY = yPos;
        screenX = xPos;
        randomize();
    }

    public Cell(int yPos, int xPos, Color color) {
        neighbours = new Neighbours();
        screenY = yPos;
        screenX = xPos;
        cellColor = color;
        alive = true;
    }

    public void randomize() {
        if (Math.random() < 0.5D) {
            int r = (int) (Math.ceil(Math.random() * 2D) * 100D);
            int g = (int) (Math.ceil(Math.random() * 2D) * 100D);
            int b = (int) (Math.ceil(Math.random() * 2D) * 100D);
            switch ((int) Math.ceil(Math.random() * 3D)) {
                case 0:
                    r = 0;
                    break;

                case 1:
                    g = 0;
                    break;

                case 2:
                    b = 0;
                    break;
            }
            cellColor = new Color(r, g, b);
            alive = true;
        } else {
            cellColor = COLOR0;
            alive = false;
        }
    }

    public void foretellFuture() {
        int numberOfNeighbours = neighbours.getNumberOfNeighbours();
        if (alive) {
            if (numberOfNeighbours < 2 || numberOfNeighbours > 3) {
                futureLife = false;
                futureColor = COLOR0;
            } else {
                futureLife = true;
                futureColor = cellColor;
            }
            return;
        }
        if (numberOfNeighbours == 3) {
            futureLife = true;
            futureColor = neighbours.getAverageColor();
        } else {
            futureLife = false;
            futureColor = COLOR0;
        }
    }

    public void realizeFuture() {
        cellColor = futureColor;
        alive = futureLife;
    }
}