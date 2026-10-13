package util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Central color/typography palette and reusable Swing component styling,
 * matching the "Fleet Intelligence" design system mockups.
 */
public final class UITheme {

    private UITheme() {}

    // ── Palette (from design mockups) ───────────────────────────────────────
    public static final Color PRIMARY            = Color.decode("#041627");
    public static final Color PRIMARY_CONTAINER   = Color.decode("#1A2B3C");
    public static final Color SECONDARY           = Color.decode("#006397");
    public static final Color SECONDARY_CONTAINER = Color.decode("#5CB8FD");
    public static final Color BACKGROUND          = Color.decode("#EEFCFD");
    public static final Color SURFACE             = Color.decode("#FFFFFF");
    public static final Color SURFACE_CONTAINER   = Color.decode("#E3F0F1");
    public static final Color SURFACE_CONTAINER_LOW = Color.decode("#E9F6F7");
    public static final Color OUTLINE_VARIANT     = Color.decode("#C4C6CD");
    public static final Color ON_SURFACE          = Color.decode("#121E1F");
    public static final Color ON_SURFACE_VARIANT  = Color.decode("#44474C");

    public static final Color ERROR             = Color.decode("#BA1A1A");
    public static final Color ERROR_CONTAINER   = Color.decode("#FFDAD6");
    public static final Color ON_ERROR_CONTAINER = Color.decode("#93000A");

    public static final Color SUCCESS_BG   = Color.decode("#E8F5E9");
    public static final Color SUCCESS_FG   = Color.decode("#2E7D32");
    public static final Color WARNING_BG   = Color.decode("#FEF9E7");
    public static final Color WARNING_FG   = Color.decode("#9A7D0A");
    public static final Color DANGER_BG    = Color.decode("#FDEDEC");
    public static final Color DANGER_FG    = Color.decode("#C0392B");
    public static final Color NEUTRAL_BG   = Color.decode("#F5F5F5");
    public static final Color NEUTRAL_FG   = Color.decode("#757575");

    // ── Typography ───────────────────────────────────────────────────────────
    private static final String FONT_FAMILY = "Segoe UI";

    public static Font fontH1()       { return new Font(FONT_FAMILY, Font.BOLD, 20); }
    public static Font fontH2()       { return new Font(FONT_FAMILY, Font.BOLD, 15); }
    public static Font fontBody()     { return new Font(FONT_FAMILY, Font.PLAIN, 13); }
    public static Font fontBodyBold() { return new Font(FONT_FAMILY, Font.BOLD, 13); }
    public static Font fontSmall()    { return new Font(FONT_FAMILY, Font.PLAIN, 11); }
    public static Font fontMono()     { return new Font(Font.MONOSPACED, Font.PLAIN, 12); }

    // ── Component styling helpers ───────────────────────────────────────────

    public static void stylePrimaryButton(JButton b) {
        b.setFont(fontBodyBold());
        b.setBackground(SECONDARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleSecondaryButton(JButton b) {
        b.setFont(fontBodyBold());
        b.setBackground(SURFACE);
        b.setForeground(PRIMARY);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(OUTLINE_VARIANT),
                new EmptyBorder(7, 16, 7, 16)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleDangerButton(JButton b) {
        b.setFont(fontBodyBold());
        b.setBackground(ERROR);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleSuccessButton(JButton b) {
        b.setFont(fontBodyBold());
        b.setBackground(SUCCESS_FG);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(fontBody());
        table.setGridColor(OUTLINE_VARIANT);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(SECONDARY_CONTAINER);
        table.setSelectionForeground(ON_SURFACE);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(fontBodyBold());
        header.setBackground(PRIMARY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
        header.setReorderingAllowed(false);
    }

    /** Zebra-striped row background renderer (applies to every column of a table). */
    public static void applyZebraRows(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? SURFACE : SURFACE_CONTAINER_LOW);
                }
                return c;
            }
        });
    }

    /** Colors + bolds a status/category column as a pill-like label based on known status keywords. */
    public static void applyStatusBadgeRenderer(JTable table, int columnIndex) {
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(fontBodyBold());
                label.setOpaque(true);
                label.setBorder(new EmptyBorder(2, 8, 2, 8));

                String status = String.valueOf(value);
                Color bg, fg;
                switch (status) {
                    case "ACTIVE", "AVAILABLE", "COMPLETED_OK" -> { bg = SUCCESS_BG; fg = SUCCESS_FG; }
                    case "CANCELLED", "DAMAGED"                 -> { bg = DANGER_BG;  fg = DANGER_FG;  }
                    case "RESERVED", "MAINTENANCE"              -> { bg = WARNING_BG; fg = WARNING_FG; }
                    case "COMPLETED"                            -> { bg = NEUTRAL_BG; fg = NEUTRAL_FG; }
                    default -> { bg = isSelected ? SECONDARY_CONTAINER : SURFACE; fg = ON_SURFACE; }
                }
                if (!isSelected) {
                    label.setBackground(bg);
                    label.setForeground(fg);
                } else {
                    label.setBackground(SECONDARY_CONTAINER);
                    label.setForeground(ON_SURFACE);
                }
                return label;
            }
        });
    }

    public static JPanel roundedHeaderBar(String title) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(PRIMARY);
        bar.setBorder(new EmptyBorder(10, 16, 10, 16));
        JLabel label = new JLabel(title);
        label.setFont(fontH2());
        label.setForeground(Color.WHITE);
        bar.add(label, BorderLayout.WEST);
        return bar;
    }
}
