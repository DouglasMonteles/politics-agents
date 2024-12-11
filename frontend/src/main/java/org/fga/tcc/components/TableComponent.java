package org.fga.tcc.components;

import org.fga.tcc.models.Table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.Serial;

public class TableComponent {

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

                if (column == 2) {
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

}
