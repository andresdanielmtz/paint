import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * A simple Java Swing application that allows users to draw on a canvas.
 * It provides tools for drawing shapes like lines, rectangles, and ovals.
 * Andrés Martínez @ 2025
 */

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

    JPanel selectedColor;

    public void setSelectedColor(Color color) {
        selectedColor.setBackground(color);
        selectedColor.repaint();
    }

    public PaintApp() {
        JFrame frame = new JFrame("Java Paint App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        DrawingPanel drawingPanel = new DrawingPanel();
        frame.add(drawingPanel, BorderLayout.CENTER);

        JPanel toolPanel = new JPanel();
        ButtonGroup toolGroup = new ButtonGroup();

        JPanel clearPanel = new JPanel();
        ButtonGroup clearGroup = new ButtonGroup();

        selectedColor = new JPanel();
        selectedColor.setBackground(drawingPanel.getCurrentColor());
        selectedColor.setPreferredSize(new Dimension(45, 45));
        toolPanel.add(selectedColor); // So it shows properly. :)

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


        JToggleButton eraserBtn = new JToggleButton("Eraser", true);
        eraserBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.ERASER));
        toolGroup.add(eraserBtn);
        toolPanel.add(eraserBtn);

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> {
            drawingPanel.shapes.clear();
            drawingPanel.repaint();
        });

        clearPanel.add(clearBtn);
        clearGroup.add(clearBtn);


        for (Color color : COLOR_PALETTE) {
            JPanel colorPanel = new JPanel();
            colorPanel.setBackground(color);
            colorPanel.setPreferredSize(new Dimension(30, 30)); // Color selector
            colorPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    // Will only set the current color if it gets clicked with the left mouse.
                    // ?? Shouldn't early return be better in this case?

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        drawingPanel.setCurrentColor(color);
                        setSelectedColor(color);
                    }
                }
            });
            toolPanel.add(colorPanel);
        }

        frame.add(clearPanel, BorderLayout.SOUTH);
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
                        case PENCIL -> {
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
                        }
                        case RECTANGLE -> currentShape = new ColoredShape(
                                    new Rectangle(
                                            Math.min(startPoint.x, e.getX()),
                                            Math.min(startPoint.y, e.getY()),
                                            Math.abs(startPoint.x - e.getX()),
                                            Math.abs(startPoint.y - e.getY())
                                    ),
                                    currentColor
                            );
                        case OVAL -> currentShape = new ColoredShape(
                                    new Ellipse2D.Double(
                                            Math.min(startPoint.x, e.getX()),
                                            Math.min(startPoint.y, e.getY()),
                                            Math.abs(startPoint.x - e.getX()),
                                            Math.abs(startPoint.y - e.getY())
                                    ),
                                    currentColor
                            );
                        case ERASER -> {
                            shapes.add(
                                    new ColoredShape(
                                            new Line2D.Double(
                                                    startPoint,
                                                    e.getPoint()
                                            ),
                                            getBackground() // Same color as background
                                    )
                            );
                            startPoint = e.getPoint();
                        }
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
                g2d.fill(coloredShape.shape);
            }


            // While we are still drawing, it means there is still something within current shape,
            // therefore it will still color.
            if (currentShape != null) {
                g2d.setColor(currentShape.color);
                g2d.draw(currentShape.shape);
                g2d.fill(currentShape.shape);

            }
        }
    }
}
