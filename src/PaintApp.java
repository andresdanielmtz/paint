import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class PaintApp {

    private static final Color[] COLOR_PALETTE = {
            Color.BLACK,
            Color.DARK_GRAY,
            Color.GRAY,
            Color.GREEN,
            Color.MAGENTA,
            Color.PINK,
            Color.RED,
            Color.BLUE,
            Color.CYAN,
            Color.YELLOW,
            Color.GRAY,
            Color.MAGENTA,
            Color.PINK
    };

    public PaintApp() {
        JFrame frame = new JFrame("Java Paint App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        DrawingPanel drawingPanel = new DrawingPanel();
        frame.add(drawingPanel, BorderLayout.CENTER);

        JPanel toolPanel = new JPanel();
        ButtonGroup toolGroup = new ButtonGroup();

        JToggleButton pencilBtn = new JToggleButton("Pencil", true);
        pencilBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.PENCIL));
        toolGroup.add(pencilBtn);
        toolPanel.add(pencilBtn);

        JToggleButton rectangleBtn = new JToggleButton("Rectangle", true);
        rectangleBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.RECTANGLE));
        toolGroup.add(rectangleBtn);
        toolPanel.add(rectangleBtn);

        JToggleButton ovalBtn = new JToggleButton("Oval", true);
        ovalBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.OVAL));
        toolGroup.add(ovalBtn);
        toolPanel.add(ovalBtn);

        JToggleButton arcBtn = new JToggleButton("Arc", true);
        arcBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.ARC));
        toolGroup.add(arcBtn);
        toolPanel.add(arcBtn);

        JToggleButton eraserBtn = new JToggleButton("Eraser", true);
        eraserBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.ERASER));
        toolGroup.add(eraserBtn);
        toolPanel.add(eraserBtn);

        for (Color color : COLOR_PALETTE) {
            JPanel colorPanel = new JPanel();
            colorPanel.setBackground(color);
            colorPanel.setPreferredSize(new Dimension(30, 30)); // Color selector
            colorPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    drawingPanel.setCurrentColor(color);
                }
            });
            toolPanel.add(colorPanel);
        }

        frame.add(toolPanel, BorderLayout.NORTH);
        frame.setSize(800, 600); // Canvas size
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new PaintApp();
    }

    enum Tool {
        PENCIL,
        RECTANGLE,
        OVAL,
        ARC,
        ERASER
    }

    class ColoredShape {
        Shape shape;
        Color color;

        public ColoredShape(Shape shape, Color color) {
            this.shape = shape;
            this.color = color;
        }
    }

    class DrawingPanel extends JPanel {
        private final List<ColoredShape> shapes = new ArrayList<>();
        private ColoredShape currentShape;
        private Point startPoint;
        private Tool currentTool = Tool.PENCIL;
        private Color currentColor = Color.BLACK;

        public DrawingPanel() {
            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    startPoint = e.getPoint();
                    currentShape = null;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (currentShape != null) {
                        shapes.add(currentShape);
                        currentShape = null;
                        repaint();
                    }
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    switch (currentTool) {
                        // TODO: Add new functionalities depending on the type of tool. It only supports the pencil as of now.
                        case PENCIL:
                            shapes.add(
                                    new ColoredShape(
                                            new Line2D.Double(
                                                    startPoint,
                                                    e.getPoint()
                                            ),
                                            currentColor
                                    )
                            );
                            startPoint = e.getPoint();
                            break;
                    }
                    repaint();
                }
            });
        }


        // Getters & Setters

        public void setCurrentTool(Tool currentTool) {
            this.currentTool = currentTool;
        }

        public Color getCurrentColor() {
            return currentColor;
        }

        public void setCurrentColor(Color color) {
            this.currentColor = color;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Draw each of the pixels with its appropriate color.
            for (ColoredShape coloredShape : shapes) {
                g2d.setColor(coloredShape.color);
                g2d.draw(coloredShape.shape);
            }


            // While we are still drawing, it means there is still something within current shape,
            // therefore it will still color.
            if (currentShape != null) {
                g2d.setColor(currentShape.color);
                g2d.draw(currentShape.shape);
            }
        }
    }
}
