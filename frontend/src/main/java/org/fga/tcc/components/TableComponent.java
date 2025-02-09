package org.fga.tcc.components;

import org.fga.tcc.models.Table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.Serial;
import java.util.concurrent.atomic.AtomicInteger;

public class TableComponent {

    private static boolean isUpdating = false;

    public static Table createTable(Object[] columnNames, Object[][] rows) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {

            @Serial
            private static final long serialVersionUID = -6180546682021760602L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // rows data
        for (var row : rows) {
            model.addRow(row);
        }

        JTable table = new JTable(model);

        DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value == null) {
                    return cell;
                }

                cell.setBackground(Color.WHITE);

                return cell;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 2) {
                tableCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                tableCellRenderer.setHorizontalAlignment(SwingConstants.LEFT);
            }

            table.getColumnModel().getColumn(i).setCellRenderer(tableCellRenderer);
        }

        return new Table(table, model);
    }

    public static Table createTableWithCheckbox(Object[] columnNames, Object[][] rows) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {

            @Serial
            private static final long serialVersionUID = 1454218790437498726L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == columnNames.length - 1;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == (columnNames.length - 1)) {
                    // Defines last column as Boolean
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        // rows data
        for (var row : rows) {
            model.addRow(row);
        }

        return new Table(new JTable(model), model);
    }

    public static Table createTableWithVotingResult(Object[] columnNames, Object[][] rows) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {

            @Serial
            private static final long serialVersionUID = -6180546682021760602L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // rows data
        for (var row : rows) {
            model.addRow(row);
        }

        JTable table = new JTable(model);

        DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value == null) {
                    return cell;
                }

                if (column == 2) {
                    Color color = (value.equals("✔")) ? Color.GREEN : Color.RED;
                    cell.setBackground(color);
                    cell.setForeground(Color.BLACK);
                } else if (column == 3) {
                    Color color = (value.equals("✔")) ? Color.GREEN : Color.RED;
                    cell.setBackground(color);
                    cell.setForeground(Color.BLACK);
                } else {
                    cell.setBackground(Color.WHITE);
                }

                return cell;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 2) {
                tableCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                tableCellRenderer.setHorizontalAlignment(SwingConstants.LEFT);
            }

            table.getColumnModel().getColumn(i).setCellRenderer(tableCellRenderer);
        }

        return new Table(table, model);
    }

    public static Table createTableVoteWithCheckbox(Object[] columnNames, Object[][] rows) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {

            @Serial
            private static final long serialVersionUID = 1454218790437498726L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == columnNames.length - 1;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == (columnNames.length - 1)) {
                    // Defines last column as Boolean
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        // rows data
        for (var row : rows) {
            model.addRow(row);
        }

        var table = new JTable(model);

        table.setDefaultRenderer(Object.class, new MultilineTableCellRenderer());
        table.setRowHeight(80);

        table.getColumnModel().getColumn(1).setPreferredWidth(800);

        table.getModel().addTableModelListener(e -> {
            if (!isUpdating && e.getColumn() == 3) {
                isUpdating = true;
                int selectedRow = e.getFirstRow();

                for (int i = 0; i < table.getRowCount(); i++) {
                    if (i != selectedRow) {
                        table.setValueAt(false, i, 3);
                    }
                }
                isUpdating = false;
            }
        });

        return new Table(table, model);
    }

    private static class MultilineTableCellRenderer extends JTextArea implements TableCellRenderer {
        public MultilineTableCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());

            // Configuração de estilos para seleção
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            return this;
        }
    }

}
