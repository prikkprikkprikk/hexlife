import java.awt.Color;

public interface HexLifeConstants {

    public static final int CELLRADIUS = 4;
    public static final String HEX = new String("hex");
    public static final int MINHEXSIZE = 7;
    public static final int MAXHEXSIZE = 100;
    public static final double HEX_HEIGHT_TO_WIDTH_RATIO = Math.sqrt(3) / 2;
    public static final String RECT = new String("rect");
    public static final int MINRECTSIZE = 10;
    public static final int MAXRECTSIZE = 150;
    public static final int GRIDSIZE_X = 5;
    public static final int SCREEN_CENTER = 320;
    public static final int GRIDSIZE_Y = 8;
    public static final int NE = 0;
    public static final int E = 1;
    public static final int SE = 2;
    public static final int SW = 3;
    public static final int W = 4;
    public static final int NW = 5;
    public static final Color COLOR0 = new Color(220, 220, 220);
    public static final Color COLOR1 = new Color(0, 200, 0);
    public static final Color COLOR2 = new Color(0, 0, 200);
    public static final int DELAY = 10;

}