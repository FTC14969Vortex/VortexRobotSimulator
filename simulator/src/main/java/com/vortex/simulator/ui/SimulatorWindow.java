package com.vortex.simulator.ui;

import com.vortex.simulator.api.Gamepad;
import com.vortex.simulator.engine.CameraSimulator;
import com.vortex.simulator.engine.RobotState;
import com.vortex.simulator.runner.OpModeScanner;
import com.vortex.simulator.runner.OpModeThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.function.Consumer;

/**
 * Main simulator window.
 *
 * Layout:
 *   ┌─────────────────────────────────────────────────────┐
 *   │  [INIT]  [START]  [STOP]   OpMode: <combo>          │  toolbar
 *   ├──────────────────────┬──────────────────────────────┤
 *   │  FieldPanel          │  CameraPanel                 │
 *   │                      │  TelemetryPanel              │
 *   └──────────────────────┴──────────────────────────────┘
 *   │  Keyboard hint bar                                    │
 *   └──────────────────────────────────────────────────────┘
 */
public class SimulatorWindow extends JFrame {

    private final FieldPanel     fieldPanel;
    private final CameraPanel    cameraPanel;
    private final TelemetryPanel telemetryPanel;
    private final JButton        initBtn;
    private final JButton        startBtn;
    private final JButton        stopBtn;
    private final JComboBox<OpModeScanner.OpModeEntry> opModeCombo;

    private OpModeThread currentThread = null;

    public SimulatorWindow(RobotState state, CameraSimulator camera, Gamepad gamepad1,
                           List<OpModeScanner.OpModeEntry> opModes,
                           Consumer<OpModeScanner.OpModeEntry> onInit) {
        super("Vortex Robot Simulator — FTC Team 14969");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        fieldPanel     = new FieldPanel(state);
        cameraPanel    = new CameraPanel(camera);
        telemetryPanel = new TelemetryPanel();

        // ── Toolbar ──────────────────────────────────────────────
        initBtn      = new JButton("INIT");
        startBtn     = new JButton("START");
        stopBtn      = new JButton("STOP");
        opModeCombo  = new JComboBox<>(opModes.toArray(new OpModeScanner.OpModeEntry[0]));

        startBtn.setEnabled(false);
        stopBtn.setEnabled(false);

        styleButton(initBtn,  new Color(40, 90, 200),  Color.WHITE);
        styleButton(startBtn, new Color(30, 160, 50),  Color.WHITE);
        styleButton(stopBtn,  new Color(190, 40, 40),  Color.WHITE);

        opModeCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel opModeLabel = new JLabel("OpMode:");
        opModeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        toolbar.setBackground(new Color(45, 45, 48));
        toolbar.add(opModeLabel);
        toolbar.add(opModeCombo);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(initBtn);
        toolbar.add(startBtn);
        toolbar.add(stopBtn);

        // ── Center layout ────────────────────────────────────────
        JSplitPane rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cameraPanel, telemetryPanel);
        rightPane.setDividerLocation(220);
        rightPane.setResizeWeight(0.45);

        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fieldPanel, rightPane);
        mainPane.setDividerLocation(500);
        mainPane.setResizeWeight(0.6);

        // ── Hint bar ─────────────────────────────────────────────
        JLabel hint = new JLabel(
            "  Arrows/WASD = drive & strafe   r = rotate CCW   R = rotate CW   b = left bumper   B = right bumper   U/O = triggers   Space = START");
        hint.setFont(new Font("Monospaced", Font.PLAIN, 11));
        hint.setForeground(new Color(180, 180, 180));
        hint.setBackground(new Color(35, 35, 38));
        hint.setOpaque(true);
        hint.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        // ── Keyboard listener (global — works regardless of which component has focus) ──
        KeyboardGamepad kbd = new KeyboardGamepad(gamepad1);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(evt -> {
            // Only handle events while this window is the active window
            if (!isActive()) return false;
            if (evt.getID() == KeyEvent.KEY_PRESSED)  kbd.keyPressed(evt);
            if (evt.getID() == KeyEvent.KEY_RELEASED) kbd.keyReleased(evt);
            return false; // don't consume — let buttons still respond to Space etc.
        });

        // ── Button actions ────────────────────────────────────────
        initBtn.addActionListener(e -> {
            OpModeScanner.OpModeEntry entry =
                (OpModeScanner.OpModeEntry) opModeCombo.getSelectedItem();
            if (entry != null) {
                if (currentThread != null) currentThread.stopOpMode();
                telemetryPanel.clear();
                onInit.accept(entry);
                initBtn.setEnabled(false);
                startBtn.setEnabled(true);
                stopBtn.setEnabled(true);
            }
        });

        startBtn.addActionListener(e -> {
            if (currentThread != null) currentThread.pressStart();
            startBtn.setEnabled(false);
        });

        stopBtn.addActionListener(e -> {
            if (currentThread != null) currentThread.stopOpMode();
            resetButtons();
        });

        // ── Assembly ──────────────────────────────────────────────
        setLayout(new BorderLayout());
        add(toolbar,  BorderLayout.NORTH);
        add(mainPane, BorderLayout.CENTER);
        add(hint,     BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
    }

    private static void styleButton(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setMargin(new Insets(6, 18, 6, 18));
        // Darken on hover
        Color hoverBg = bg.darker();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(hoverBg);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(bg);
                else btn.setBackground(bg.darker().darker());
            }
        });
        // Restore correct color when enabled state changes
        btn.addPropertyChangeListener("enabled", evt -> {
            btn.setBackground(btn.isEnabled() ? bg : bg.darker().darker());
            btn.setForeground(btn.isEnabled() ? fg : new Color(180, 180, 180));
        });
    }

    public void setCurrentThread(OpModeThread t) {
        this.currentThread = t;
    }

    public TelemetryPanel getTelemetryPanel() { return telemetryPanel; }

    private void resetButtons() {
        SwingUtilities.invokeLater(() -> {
            initBtn.setEnabled(true);
            startBtn.setEnabled(false);
            stopBtn.setEnabled(false);
        });
    }

    public void notifyOpModeStopped() {
        resetButtons();
    }
}
