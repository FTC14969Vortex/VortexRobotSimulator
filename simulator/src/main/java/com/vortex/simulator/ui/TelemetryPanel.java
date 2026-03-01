package com.vortex.simulator.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Scrolling key=value telemetry display.
 * Updates are posted from TelemetryImpl via SwingUtilities.invokeLater.
 */
public class TelemetryPanel extends JPanel {

    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list;

    public TelemetryPanel() {
        super(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY), "Telemetry",
            0, 0, new Font("Monospaced", Font.PLAIN, 11), Color.LIGHT_GRAY));

        list = new JList<>(model);
        list.setBackground(Color.BLACK);
        list.setForeground(new Color(0, 230, 0));
        list.setFont(new Font("Monospaced", Font.PLAIN, 12));
        list.setSelectionBackground(Color.DARK_GRAY);

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBackground(Color.BLACK);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    /** Called from SwingUtilities.invokeLater — always on EDT. */
    public void setLines(List<String> lines) {
        model.clear();
        lines.forEach(model::addElement);
    }

    public void clear() {
        model.clear();
    }
}
