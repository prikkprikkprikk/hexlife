import java.awt.Color;

public class Cell
    implements HexLifeConstants
{

    public Cell(int i, int j)
    {
        neighbours = new Neighbours();
        screen_y = i;
        screen_x = j;
        randomize();
    }

    public Cell(int i, int j, Color color)
    {
        neighbours = new Neighbours();
        screen_y = i;
        screen_x = j;
        cellColor = color;
        alive = true;
    }

    public void randomize()
    {
        if(Math.random() < 0.5D)
        {
            int i = (int)(Math.ceil(Math.random() * 2D) * 100D);
            int j = (int)(Math.ceil(Math.random() * 2D) * 100D);
            int k = (int)(Math.ceil(Math.random() * 2D) * 100D);
            switch((int)Math.ceil(Math.random() * 3D))
            {
            case 1: // '\001'
                i = 0;
                break;

            case 2: // '\002'
                j = 0;
                break;

            case 3: // '\003'
                k = 0;
                break;
            }
            cellColor = new Color(i, j, k);
            alive = true;
            return;
        } else
        {
            cellColor = HexLifeConstants.COLOR0;
            alive = false;
            return;
        }
    }

    public void foretellFuture()
    {
        int i = neighbours.getNumberOfNeighbours();
        if(alive)
            if(i < 2 || i > 3)
            {
                futureLife = false;
                futureColor = HexLifeConstants.COLOR0;
                return;
            } else
            {
                futureLife = true;
                futureColor = cellColor;
                return;
            }
        if(i == 3)
        {
            futureLife = true;
            futureColor = neighbours.getAverageColor();
            return;
        } else
        {
            futureLife = false;
            futureColor = HexLifeConstants.COLOR0;
            return;
        }
    }

    public void realizeFuture()
    {
        cellColor = futureColor;
        alive = futureLife;
    }

    public boolean alive;
    public Color cellColor;
    public int screen_x;
    public int screen_y;
    public Neighbours neighbours;
    public boolean futureLife;
    public Color futureColor;
}