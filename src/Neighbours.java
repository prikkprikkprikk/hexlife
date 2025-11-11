import java.awt.Color;

public class Neighbours
    implements HexLifeConstants
{

    public Neighbours()
    {
        neighbour = new Cell[6];
    }

    public void setNeighbours(Cell cell, Cell cell1, Cell cell2, Cell cell3, Cell cell4, Cell cell5)
    {
        neighbour[0] = cell;
        neighbour[1] = cell1;
        neighbour[2] = cell2;
        neighbour[3] = cell3;
        neighbour[4] = cell4;
        neighbour[5] = cell5;
    }

    public int getNumberOfNeighbours()
    {
        int i = 0;
        for(int j = 0; j < 6; j++)
            if(neighbour[j].alive)
                i++;

        return i;
    }

    public Color getAverageColor()
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        for(int i1 = 0; i1 < 6; i1++)
            if(neighbour[i1].alive)
            {
                i++;
                j += neighbour[i1].cellColor.getRed();
                k += neighbour[i1].cellColor.getBlue();
                l += neighbour[i1].cellColor.getGreen();
            }

        if(i == 0)
            return HexLifeConstants.COLOR0;
        int j1 = j / i;
        int k1 = k / i;
        int l1 = l / i;
        int i2 = j + k + l;
        int j2 = (j1 + k1 + l1) * i;
        int k2 = i2 - j2;
        if(k2 != 0)
            switch((int)Math.floor(Math.random() * 3D))
            {
            case 1: // '\001'
                j1 += k2;
                break;

            case 2: // '\002'
                k1 += k2;
                break;

            case 3: // '\003'
                l1 += k2;
                break;
            }
        Color color = new Color(j1, k1, l1);
        return color;
    }

    public Cell neighbour[];
}