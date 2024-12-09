package org.fga.tcc.components;

import org.fga.tcc.models.Table;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

}
