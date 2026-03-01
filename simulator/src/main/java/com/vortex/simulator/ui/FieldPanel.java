package com.vortex.simulator.ui;

import com.vortex.simulator.engine.AprilTagLayout;
import com.vortex.simulator.engine.RobotState;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * 144"×144" overhead field view.
 * Robot: filled triangle pointing in heading direction.
 * AprilTags: colored numbered squares.
 * Redraws at ~30 Hz via Swing Timer.
 */
public class FieldPanel extends JPanel {

    private static final int ROBOT_SIZE_PX = 14; // half-side in pixels
    private static final int TAG_SIZE_PX   =  8;

    private final RobotState state;

    public FieldPanel(RobotState state) {
        this.state = state;
        setBackground(new Color(30, 60, 30)); // dark green field
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        Timer t = new Timer(33, e -> repaint());
        t.start();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 500);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Draw field tiles grid
        g2.setColor(new Color(40, 80, 40));
        int tileCount = 6;
        for (int i = 0; i <= tileCount; i++) {
            int x = i * w / tileCount;
            int y = i * h / tileCount;
            g2.drawLine(x, 0, x, h);
            g2.drawLine(0, y, w, y);
        }

        // Field boundary
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(0, 0, w - 1, h - 1);

        // Draw AprilTags
        for (AprilTagLayout.Tag tag : AprilTagLayout.TAGS) {
            int px = fieldToPixelX(tag.x(), w);
            int py = fieldToPixelY(tag.y(), h);

            // Tag square
            g2.setColor(new Color(255, 200, 0));
            g2.fillRect(px - TAG_SIZE_PX, py - TAG_SIZE_PX, TAG_SIZE_PX * 2, TAG_SIZE_PX * 2);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(px - TAG_SIZE_PX, py - TAG_SIZE_PX, TAG_SIZE_PX * 2, TAG_SIZE_PX * 2);

            // Tag ID label
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Monospaced", Font.BOLD, 9));
            g2.drawString(String.valueOf(tag.id()), px - 5, py + 4);
        }

        // Draw robot
        double robotX  = state.getX();
        double robotY  = state.getY();
        double heading = state.getHeadingRad();

        int rpx = fieldToPixelX(robotX, w);
        int rpy = fieldToPixelY(robotY, h);

        AffineTransform savedTx = g2.getTransform();
        g2.translate(rpx, rpy);
        // In field coords: heading 0 = facing right (+X), π/2 = facing up (+Y).
        // In screen coords: +X is right, +Y is down, so flip Y.
        g2.rotate(-heading);

        // Robot body: filled square
        g2.setColor(new Color(30, 100, 220));
        g2.fillRect(-ROBOT_SIZE_PX, -ROBOT_SIZE_PX, ROBOT_SIZE_PX * 2, ROBOT_SIZE_PX * 2);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1));
        g2.drawRect(-ROBOT_SIZE_PX, -ROBOT_SIZE_PX, ROBOT_SIZE_PX * 2, ROBOT_SIZE_PX * 2);

        // Direction arrow (pointing forward = +X in robot frame)
        g2.setColor(Color.CYAN);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, 0, ROBOT_SIZE_PX + 4, 0);
        // Arrowhead
        int[] ax = {ROBOT_SIZE_PX + 4, ROBOT_SIZE_PX - 2, ROBOT_SIZE_PX - 2};
        int[] ay = {0, -4, 4};
        g2.fillPolygon(ax, ay, 3);

        g2.setTransform(savedTx);

        // Coordinate label
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        double headingDeg = ((Math.toDegrees(heading) % 360) + 360) % 360;
        g2.drawString(String.format("(%.1f\", %.1f\") %.0f°",
            robotX, robotY, headingDeg), 4, h - 6);
    }

    private int fieldToPixelX(double fieldX, int panelW) {
        // field: [-72, 72] → pixel [0, panelW]
        return (int)((fieldX + 72.0) / 144.0 * panelW);
    }

    private int fieldToPixelY(double fieldY, int panelH) {
        // field: [-72, 72] → pixel [panelH, 0]  (Y flipped)
        return (int)((1.0 - (fieldY + 72.0) / 144.0) * panelH);
    }
}
