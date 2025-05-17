import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class PaintApp {
    private static final Color[] COLOR_PALETTE = {
            Color.BLACK,
            Color.DARK_GRAY,
            Color.GRAY,
    };

    public static void main(String[] args) throws Exception {
        new PaintApp();
    }

    enum Tool {
        PENCIL
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
        private final ArrayList<ColoredShape> shapes = new ArrayList<>();
        private ColoredShape currentShape;

        private Point startPoint;
        private Tool currentTool;
        private Color currentColor;

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


        public PaintApp() {
            JFrame frame = new JFrame("Java Paint App");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
            if (currentTool != null) {
                g2d.setColor(currentShape.color);
                g2d.draw(currentShape.shape);
            }
        }
    }
}
