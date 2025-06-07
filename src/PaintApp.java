import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple Java Swing application that allows users to draw on a canvas.
 * It provides tools for drawing shapes like lines, rectangles, and ovals.
 * Andrés Martínez @ 2025
 */

public class PaintApp {

    private static final Color[] COLOR_PALETTE = {
            new Color(231, 76, 60), // Flat Red
            new Color(46, 204, 113), // Flat Green
            new Color(52, 152, 219), // Flat Blue
            new Color(155, 89, 182), // Flat Purple
            new Color(241, 196, 15), // Flat Yellow
            new Color(230, 126, 34), // Flat Orange
            new Color(26, 188, 156), // Flat Turquoise
            new Color(236, 240, 241), // Flat Light Gray
            new Color(44, 62, 80) // Flat Dark Blue
    };

    JPanel selectedColor;
    JPanel selectedBorderColor;

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
        selectedBorderColor = new JPanel();
        selectedBorderColor.setBackground(drawingPanel.getCurrentBorderColor());
        selectedBorderColor.setPreferredSize(new Dimension(45, 45));
        toolPanel.add(selectedBorderColor);

        selectedColor = new JPanel();
        selectedColor.setBackground(drawingPanel.getCurrentColor());
        selectedColor.setPreferredSize(new Dimension(45, 45));
        toolPanel.add(selectedColor); // So it shows properly. :)


        // TODO: Add icons for the tools.
        ImageIcon pencilIcon = new ImageIcon("icons/pencil.png");
        pencilIcon = new ImageIcon(pencilIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JToggleButton pencilBtn = new JToggleButton("Pencil", true);
        pencilBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.PENCIL));
        pencilBtn.setIcon(pencilIcon);
        toolGroup.add(pencilBtn);
        toolPanel.add(pencilBtn);

        ImageIcon rectangleIcon = new ImageIcon("icons/rectangle.png");
        rectangleIcon = new ImageIcon(rectangleIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JToggleButton rectangleBtn = new JToggleButton("Rectangle", true);
        rectangleBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.RECTANGLE));
        rectangleBtn.setIcon(rectangleIcon);
        toolGroup.add(rectangleBtn);
        toolPanel.add(rectangleBtn);

        ImageIcon ovalIcon = new ImageIcon("icons/oval.png");
        ovalIcon = new ImageIcon(ovalIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JToggleButton ovalBtn = new JToggleButton("Oval", true);
        ovalBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.OVAL));
        ovalBtn.setIcon(ovalIcon);
        toolGroup.add(ovalBtn);
        toolPanel.add(ovalBtn);

        ImageIcon eraserIcon = new ImageIcon("icons/eraser.png");
        eraserIcon = new ImageIcon(eraserIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JToggleButton eraserBtn = new JToggleButton("Eraser", true);
        eraserBtn.addActionListener(e -> drawingPanel.setCurrentTool(Tool.ERASER));
        eraserBtn.setIcon(eraserIcon);
        toolGroup.add(eraserBtn);
        toolPanel.add(eraserBtn);

        ImageIcon clearIcon = new ImageIcon("icons/clear.png");
        clearIcon = new ImageIcon(clearIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton clearBtn = new JButton("Clear");
        clearBtn.setIcon(clearIcon);
        clearBtn.addActionListener(e -> {
            drawingPanel.shapes.clear();
            drawingPanel.repaint();
        });

        clearPanel.add(clearBtn);
        clearGroup.add(clearBtn);

        // Add a slider for stroke width
        JLabel strokeLabel = new JLabel("Stroke: 1");
        JSlider strokeSlider = new JSlider(1, 30, 1);
        strokeSlider.setPaintTrack(true);
        strokeSlider.setPaintTicks(true);
        strokeSlider.setPaintLabels(true);
        strokeSlider.setMinorTickSpacing(10);
        strokeSlider.setMajorTickSpacing(5);
        strokeSlider.addChangeListener(
                // Lambda expression to handle slider changes
                e -> {
                    strokeLabel.setText("Stroke: " + strokeSlider.getValue());
                    // TODO: Implement set stroke of the shape
                    drawingPanel.setStrokeWidth(strokeSlider.getValue());
                }
        );

        toolPanel.add(strokeSlider);
        toolPanel.add(strokeLabel);

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
                        drawingPanel.setCurrentBorderColor(color);
                        setSelectedBorderColor(color);
                    } else {
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
        SwingUtilities.invokeLater(() -> new PaintApp());
    }

    public void setSelectedColor(Color color) {
        selectedColor.setBackground(color);
        selectedColor.repaint();
    }

    public void setSelectedBorderColor(Color color) {
        selectedBorderColor.setBackground(color);
        selectedBorderColor.repaint();
    }

    enum Tool {
        PENCIL,
        RECTANGLE,
        OVAL,
        ERASER
    }

    class ColoredShape {

        Shape shape;
        Color fillColor;
        Color borderColor;
        float strokeWidth;

        public ColoredShape(Shape shape, Color fillColor, Color borderColor, float strokeWidth) {
            this.shape = shape;
            this.fillColor = fillColor;
            this.borderColor = borderColor;
            this.strokeWidth = strokeWidth;
        }
    }

    class DrawingPanel extends JPanel {
        private final List<ColoredShape> shapes = new ArrayList<>();
        private ColoredShape currentShape;
        private Point startPoint;
        private Tool currentTool = Tool.PENCIL;
        private Color currentColor = Color.BLACK;
        private Color currentBorderColor = Color.WHITE;
        private float strokeWidth = 1.0f;


        public DrawingPanel() {
            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        return;
                    }
                    startPoint = e.getPoint();
                    currentShape = null;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        return;
                    }
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
                    if (SwingUtilities.isRightMouseButton(e)) {
                        return;
                    }
                    switch (currentTool) {

                        case PENCIL -> {
                            shapes.add(
                                    new ColoredShape(
                                            new Line2D.Double(
                                                    startPoint,
                                                    e.getPoint()),
                                            currentColor, currentBorderColor
                                            , strokeWidth));
                            startPoint = e.getPoint();
                        }
                        case RECTANGLE -> currentShape = new ColoredShape(
                                new Rectangle(
                                        Math.min(startPoint.x, e.getX()),
                                        Math.min(startPoint.y, e.getY()),
                                        Math.abs(startPoint.x - e.getX()),
                                        Math.abs(startPoint.y - e.getY())),
                                currentColor, currentBorderColor, strokeWidth);
                        case OVAL -> currentShape = new ColoredShape(
                                new Ellipse2D.Double(
                                        Math.min(startPoint.x, e.getX()),
                                        Math.min(startPoint.y, e.getY()),
                                        Math.abs(startPoint.x - e.getX()),
                                        Math.abs(startPoint.y - e.getY())),
                                currentColor,
                                currentBorderColor, strokeWidth);
                        case ERASER -> {
                            Color bg = getBackground();
                            shapes.add(
                                    new ColoredShape(
                                            new Line2D.Double(
                                                    startPoint,
                                                    e.getPoint()),
                                            bg,
                                            bg,
                                            strokeWidth
                                    ));
                            startPoint = e.getPoint();
                        }
                    }
                    repaint();
                }
            });
        }

        // Getters & Setters

        public void setStrokeWidth(float strokeWidth) {
            this.strokeWidth = strokeWidth;
        }

        public void setCurrentTool(Tool currentTool) {
            this.currentTool = currentTool;
        }

        public Color getCurrentColor() {
            return currentColor;
        }

        public void setCurrentColor(Color color) {
            this.currentColor = color;
        }

        public Color getCurrentBorderColor() {
            return currentBorderColor;
        }

        public void setCurrentBorderColor(Color color) {
            this.currentBorderColor = color;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Draw each of the pixels with its appropriate color.
            for (ColoredShape coloredShape : shapes) {
                g2d.setStroke(new BasicStroke(coloredShape.strokeWidth));
                g2d.setColor(coloredShape.fillColor);
                g2d.draw(coloredShape.shape);
                g2d.fill(coloredShape.shape);
                g2d.setColor(coloredShape.borderColor);
                g2d.draw(coloredShape.shape);
            }

            // While we are still drawing, it means there is still something within current
            // shape,
            // therefore it will still color.
            if (currentShape != null) {
                g2d.setColor(currentShape.fillColor);
                g2d.draw(currentShape.shape);
                g2d.fill(currentShape.shape);
                g2d.setColor(currentShape.borderColor);
                g2d.draw(currentShape.shape);
            }
        }
    }
}
