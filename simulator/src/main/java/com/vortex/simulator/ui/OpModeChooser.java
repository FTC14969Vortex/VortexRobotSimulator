package com.vortex.simulator.ui;

import com.vortex.simulator.runner.OpModeScanner;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Modal dialog that lists all discovered @TeleOp / @Autonomous classes.
 * Returns the selected class, or null if the user cancels.
 */
public class OpModeChooser extends JDialog {

    private Class<?> selected = null;

    public OpModeChooser(Frame parent, List<OpModeScanner.OpModeEntry> entries) {
        super(parent, "Select OpMode", true);
        setLayout(new BorderLayout(8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Group entries by type (TeleOp first, then Autonomous)
        DefaultListModel<OpModeScanner.OpModeEntry> model = new DefaultListModel<>();
        entries.stream()
            .filter(e -> e.isTeleOp())
            .forEach(model::addElement);
        entries.stream()
            .filter(e -> !e.isTeleOp())
            .forEach(model::addElement);

        JList<OpModeScanner.OpModeEntry> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new OpModeRenderer());
        list.setFont(new Font("Monospaced", Font.PLAIN, 13));
        if (model.size() > 0) list.setSelectedIndex(0);

        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(420, 280));
        add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok     = new JButton("Select");
        JButton cancel = new JButton("Cancel");

        ok.addActionListener(e -> {
            selected = list.getSelectedValue() != null
                ? list.getSelectedValue().opModeClass()
                : null;
            dispose();
        });
        cancel.addActionListener(e -> dispose());

        list.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) ok.doClick();
            }
        });

        getRootPane().setDefaultButton(ok);
        buttons.add(cancel);
        buttons.add(ok);
        add(buttons, BorderLayout.SOUTH);

        if (model.isEmpty()) {
            ok.setEnabled(false);
            add(new JLabel("No @TeleOp or @Autonomous classes found on classpath.",
                SwingConstants.CENTER), BorderLayout.NORTH);
        }

        pack();
        setLocationRelativeTo(parent);
    }

    public Class<?> getSelected() { return selected; }

    private static class OpModeRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof OpModeScanner.OpModeEntry e) {
                String tag = e.isTeleOp() ? "[TeleOp]  " : "[Auto]    ";
                setText(tag + e.displayName() + "  (" + e.group() + ")");
                setForeground(isSelected ? Color.WHITE :
                    e.isTeleOp() ? new Color(100, 180, 255) : new Color(150, 255, 150));
            }
            return this;
        }
    }
}
