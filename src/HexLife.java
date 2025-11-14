import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HexLife extends JFrame implements Runnable, ActionListener, HexLifeConstants {
    private JPanel canvas;
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

    public HexLife() {
        messageFont = new Font("Verdana", 0, fontSize);
        boardShape = HexLifeConstants.RECT;
        boardSize = 90;
        board = new Board(boardShape, boardSize);
        boardChanged = false;
        frozen = true;
        sizeLabel = new Label("Size:", 2);
        shapeLabel = new Label("Shape:", 2);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HexLife game = new HexLife();
            game.init();
            game.setVisible(true);
        });
    }

    public void init() {
        if (RECT.equals(boardShape)) {
            minSize = MINRECTSIZE;
            maxSize = MAXRECTSIZE;
        } else {
            minSize = MINHEXSIZE;
            maxSize = MAXHEXSIZE;
        }

        // Set up the main frame
        setTitle("HexLife - Conway's Game of Life");
        setSize(SCREEN_SIZE_X, SCREEN_SIZE_Y);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create control panel at the top
        JPanel controlPanel = new JPanel(new FlowLayout());

        animateButton = new Button("Animate");
        animateButton.setActionCommand("animate");
        animateButton.addActionListener(this);
        controlPanel.add(animateButton);
        stepButton = new Button("Step");
        stepButton.setActionCommand("step");
        stepButton.addActionListener(this);
        controlPanel.add(stepButton);
        randomizeButton = new Button("Randomize");
        randomizeButton.setActionCommand("randomize");
        randomizeButton.addActionListener(this);
        controlPanel.add(randomizeButton);
        controlPanel.add(sizeLabel);
        sizeField = new TextField(String.valueOf(boardSize), 2);
        sizeField.addActionListener(this);
        controlPanel.add(sizeField);
        shapeGroup = new CheckboxGroup();
        rectShape = new Checkbox("Rect", shapeGroup, true);
        hexShape = new Checkbox("Hex", shapeGroup, false);
        controlPanel.add(shapeLabel);
        controlPanel.add(rectShape);
        controlPanel.add(hexShape);
        changeButton = new Button("Change");
        changeButton.setActionCommand("change");
        changeButton.addActionListener(this);
        controlPanel.add(changeButton);

        add(controlPanel, BorderLayout.NORTH);

        // Create canvas panel for drawing
        canvas = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                HexLife.this.paintCanvas(g);
            }
        };
        canvas.setBackground(Color.WHITE);
        add(canvas, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent actionevent) {
        String actionCommand = actionevent.getActionCommand();
        if ("animate".equals(actionCommand))
            if (frozen) {
                frozen = false;
                animateButton.setLabel("Stop");
                randomizeButton.setEnabled(false);
                stepButton.setEnabled(false);
                sizeField.setEditable(false);
                changeButton.setEnabled(false);
                start();
                return;
            } else {
                frozen = true;
                animateButton.setLabel("Animate");
                randomizeButton.setEnabled(true);
                stepButton.setEnabled(true);
                sizeField.setEditable(true);
                changeButton.setEnabled(true);
                stop();
                return;
            }
        if ("randomize".equals(actionCommand)) {
            board.randomize();
            canvas.repaint();
            return;
        }
        if ("step".equals(actionCommand)) {
            board.advance();
            canvas.repaint();
            return;
        }
        if ("change".equals(actionCommand)) {
            boolean flag = false;
            Checkbox checkbox = shapeGroup.getSelectedCheckbox();
            if (checkbox == rectShape) {
                if (HEX.equals(boardShape)) {
                    minSize = MINRECTSIZE;
                    maxSize = MAXRECTSIZE;
                    boardShape = HexLifeConstants.RECT;
                    flag = true;
                }
            } else if (RECT.equals(boardShape)) {
                minSize = MINHEXSIZE;
                maxSize = MAXHEXSIZE;
                boardShape = HexLifeConstants.HEX;
                flag = true;
            }
            String sizeFieldValue = sizeField.getText();
            int requestedSize = Integer.decode(sizeFieldValue).intValue();
            if (flag) {
                if (requestedSize < minSize)
                    requestedSize = minSize;
                else if (requestedSize > maxSize)
                    requestedSize = maxSize;
                boardSize = requestedSize;
                board = new Board(boardShape, boardSize);
                sizeField.setText(String.valueOf(boardSize));
                boardChanged = true;
                canvas.repaint();
                return;
            }
            if (requestedSize < minSize || requestedSize > maxSize) {
                sizeField.setText(String.valueOf(boardSize));
                return;
            }
            boardSize = requestedSize;
            board = new Board(boardShape, boardSize);
            boardChanged = true;
            canvas.repaint();
        }
    }

    public void start() {
        if (!frozen) {
            if (lifeThread == null)
                lifeThread = new Thread(this);
            lifeThread.start();
        }
    }

    public void stop() {
        lifeThread = null;
        canvas.repaint();
    }

    public void run() {
        Thread currentThread = Thread.currentThread();
        while (currentThread == lifeThread) {
            board.advance();
            canvas.repaint();
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException _ex) {
                return;
            }
        }
    }

    public void setDrawClip(Graphics g) {
        g.setClip(1, 1, canvas.getSize().width - 2, canvas.getSize().height - 2);
    }

    public void displayMessage(Graphics g, String message) {
        g.setColor(Color.white);
        g.fillRect(1, 1, canvas.getSize().width - 2, 25);
        g.setFont(messageFont);
        g.setColor(Color.black);
        g.drawString(message, 10, 20);
    }

    public void paintCanvas(Graphics g) {
        g.setColor(Color.black);
        g.drawRect(0, 0, canvas.getSize().width - 1, canvas.getSize().height - 1);
        g.setColor(Color.white);
        g.fillRect(1, 1, canvas.getSize().width - 2, canvas.getSize().height - 2);
        setDrawClip(g);

        if (boardChanged) {
            boardChanged = false;
        }
        displayMessage(g, "Gen: " + board.generation);
        for (int currentRow = 0; currentRow < board.numberOfRows; currentRow++) {
            for (int currentCol = 0; currentCol < board.boardArray[currentRow].length; currentCol++) {
                Cell cell = board.boardArray[currentRow][currentCol];
                g.setColor(cell.cellColor);
                g.fillOval(cell.screenX - CELLRADIUS, cell.screenY - CELLRADIUS, (CELLRADIUS * 2), (CELLRADIUS * 2));
            }
        }
    }
}
