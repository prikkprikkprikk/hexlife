import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HexLife extends Applet implements Runnable, ActionListener, HexLifeConstants
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void init()
    {
        if(boardShape == HexLifeConstants.RECT)
        {
            minSize = 10;
            maxSize = 60;
        } else
        {
            minSize = 7;
            maxSize = 30;
        }
        animateButton = new Button("Animate");
        animateButton.setActionCommand("animate");
        animateButton.addActionListener(this);
        add(animateButton);
        stepButton = new Button("Step");
        stepButton.setActionCommand("step");
        stepButton.addActionListener(this);
        add(stepButton);
        randomizeButton = new Button("Randomize");
        randomizeButton.setActionCommand("randomize");
        randomizeButton.addActionListener(this);
        add(randomizeButton);
        add(sizeLabel);
        sizeField = new TextField(String.valueOf(boardSize), 2);
        sizeField.addActionListener(this);
        add(sizeField);
        shapeGroup = new CheckboxGroup();
        rectShape = new Checkbox("Rect", shapeGroup, true);
        hexShape = new Checkbox("Hex", shapeGroup, false);
        add(shapeLabel);
        add(rectShape);
        add(hexShape);
        changeButton = new Button("Change");
        changeButton.setActionCommand("change");
        changeButton.addActionListener(this);
        add(changeButton);
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        String s = actionevent.getActionCommand();
        if(s == "animate")
            if(frozen)
            {
                frozen = false;
                animateButton.setLabel("Stop");
                randomizeButton.setEnabled(false);
                stepButton.setEnabled(false);
                sizeField.setEditable(false);
                changeButton.setEnabled(false);
                start();
                return;
            } else
            {
                frozen = true;
                animateButton.setLabel("Animate");
                randomizeButton.setEnabled(true);
                stepButton.setEnabled(true);
                sizeField.setEditable(true);
                changeButton.setEnabled(true);
                stop();
                return;
            }
        if(s == "randomize")
        {
            board.randomize();
            repaint();
            return;
        }
        if(s == "step")
        {
            board.advance();
            repaint();
            return;
        }
        if(s == "change")
        {
            boolean flag = false;
            Checkbox checkbox = shapeGroup.getSelectedCheckbox();
            if(checkbox == rectShape)
            {
                if(boardShape == HexLifeConstants.HEX)
                {
                    minSize = 10;
                    maxSize = 60;
                    boardShape = HexLifeConstants.RECT;
                    flag = true;
                }
            } else
            if(boardShape == HexLifeConstants.RECT)
            {
                minSize = 7;
                maxSize = 30;
                boardShape = HexLifeConstants.HEX;
                flag = true;
            }
            String s1 = sizeField.getText();
            int i = Integer.decode(s1).intValue();
            if(flag)
            {
                if(i < minSize)
                    i = minSize;
                else
                if(i > maxSize)
                    i = maxSize;
                boardSize = i;
                board = new Board(boardShape, boardSize);
                sizeField.setText(String.valueOf(boardSize));
                boardChanged = true;
                repaint();
                return;
            }
            if(i < minSize || i > maxSize)
            {
                sizeField.setText(String.valueOf(boardSize));
                return;
            }
            boardSize = i;
            board = new Board(boardShape, boardSize);
            boardChanged = true;
            repaint();
        }
    }

    public void start()
    {
        if(!frozen)
        {
            if(lifeThread == null)
                lifeThread = new Thread(this);
            lifeThread.start();
        }
    }

    public void stop()
    {
        lifeThread = null;
        repaint();
    }

    public void run()
    {
        Thread.currentThread().setPriority(1);
        for(Thread thread = Thread.currentThread(); thread == lifeThread;)
        {
            board.advance();
            repaint();
            try
            {
                Thread.sleep(10L);
            }
            catch(InterruptedException _ex)
            {
                return;
            }
        }

    }

    public void setDrawClip(Graphics g)
    {
        g.setClip(1, 1, getSize().width - 2, getSize().height - 2);
    }

    public void displayMessage(Graphics g, String s)
    {
        g.setColor(Color.white);
        g.fillRect(1, 1, getSize().width - 2, 25);
        g.setFont(messageFont);
        g.setColor(Color.black);
        g.drawString(s, 10, 20);
    }

    public void paint(Graphics g)
    {
        g.setColor(Color.black);
        g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
        g.setColor(Color.white);
        g.fillRect(1, 1, getSize().width - 2, getSize().height - 2);
        setDrawClip(g);
        update(g);
    }

    public void update(Graphics g)
    {
        if(boardChanged)
        {
            boardChanged = false;
            paint(g);
        }
        displayMessage(g, "Gen: " + board.generation);
        for(int i = 0; i < board.numberOfRows; i++)
        {
            for(int j = 0; j < board.boardArray[i].length; j++)
            {
                Cell cell = board.boardArray[i][j];
                g.setColor(cell.cellColor);
                g.fillOval(cell.screen_x - 4, cell.screen_y - 4, 8, 8);
            }

        }

    }

    public HexLife()
    {
        messageFont = new Font("Verdana", 0, 10);
        boardShape = HexLifeConstants.RECT;
        boardSize = 60;
        board = new Board(boardShape, boardSize);
        boardChanged = false;
        frozen = true;
        sizeLabel = new Label("Size:", 2);
        shapeLabel = new Label("Shape:", 2);
    }

    final int fontSize = 10;
    Font messageFont;
    String boardShape;
    int boardSize;
    Board board;
    int minSize;
    int maxSize;
    boolean boardChanged;
    Thread lifeThread;
    boolean frozen;
    static final String ANIMATE = "animate";
    Button animateButton;
    static final String RANDOMIZE = "randomize";
    Button randomizeButton;
    static final String STEP = "step";
    Button stepButton;
    Label sizeLabel;
    TextField sizeField;
    static final String CHANGE = "change";
    Button changeButton;
    Label shapeLabel;
    Checkbox rectShape;
    Checkbox hexShape;
    CheckboxGroup shapeGroup;
}