package com.vortex.simulator.ui;

import com.vortex.simulator.engine.CameraSimulator;

import javax.swing.*;
import java.awt.*;

/**
 * Simulated camera viewfinder showing a reticle for the visible AprilTag.
 * Redraws at ~30 Hz via a Swing Timer.
 */
public class CameraPanel extends JPanel {

    private static final int PANEL_W = 320;
    private static final int PANEL_H = 200;
    private static final double FOV_DEG = 30.0;

    private final CameraSimulator camera;

    public CameraPanel(CameraSimulator camera) {
        this.camera = camera;
        setPreferredSize(new Dimension(PANEL_W, PANEL_H));
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY), "Camera",
            0, 0, new Font("SansSerif", Font.PLAIN, 11), Color.LIGHT_GRAY));

        Timer t = new Timer(33, e -> repaint());
        t.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Dark background gradient
        g2.setPaint(new GradientPaint(0, 0, new Color(10, 10, 30), 0, h, Color.BLACK));
        g2.fillRect(0, 0, w, h);

        // Grid lines
        g2.setColor(new Color(30, 30, 60));
        g2.setStroke(new BasicStroke(1));
        for (int x = 0; x < w; x += w / 6) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += h / 4) g2.drawLine(0, y, w, y);

        // Center crosshair
        g2.setColor(new Color(60, 60, 80));
        g2.drawLine(w / 2, 0, w / 2, h);
        g2.drawLine(0, h / 2, w, h / 2);

        int tagId = camera.getLastVisibleTagId();
        if (tagId >= 0) {
            double txDeg = camera.getLastTxDeg();
            // Map txDeg ∈ [-FOV, +FOV] → pixel X
            int px = (int)((txDeg / FOV_DEG + 1.0) / 2.0 * w);
            int py = h / 2; // simplified — tag at horizon

            // Draw reticle
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(2));
            int r = 20;
            g2.drawOval(px - r, py - r, r * 2, r * 2);
            g2.drawLine(px - r - 8, py, px - r + 2, py);
            g2.drawLine(px + r - 2, py, px + r + 8, py);
            g2.drawLine(px, py - r - 8, px, py - r + 2);
            g2.drawLine(px, py + r - 2, px, py + r + 8);

            // Label
            g2.setFont(new Font("Monospaced", Font.BOLD, 12));
            g2.setColor(Color.YELLOW);
            g2.drawString("ID " + tagId, px + r + 4, py - 4);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
            g2.drawString(String.format("tx=%.1f°", txDeg), px + r + 4, py + 12);
        } else {
            // No target
            g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g2.setColor(new Color(100, 100, 100));
            String msg = "No target";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2 + fm.getAscent() / 2);
        }

        // FOV arc indicator
        g2.setColor(new Color(80, 80, 40));
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1,
            new float[]{4, 4}, 0));
        g2.drawLine(0, h - 10, w / 2, h / 2);
        g2.drawLine(w, h - 10, w / 2, h / 2);
    }
}
