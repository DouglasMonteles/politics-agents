package org.fga.tcc.components;

import org.fga.tcc.models.Button;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class ButtonComponent {

    public static Button createTableButton(String label, DefaultTableModel model,
                                           int valueColumnIndex, JFrame jFrame,
                                           HandleActionButton handleInfo, String proposal) {
        JButton button = new JButton(label);
        java.util.List<Object> selected = new ArrayList<>();

        button.addActionListener(e -> {
            for (int i = 0; i < model.getRowCount(); i++) {
                try {
                    Boolean isSelected = (Boolean) model.getValueAt(i, model.getColumnCount() - 1);

                    if (isSelected != null && isSelected) {
                        if (valueColumnIndex != -1) {
                            selected.add(model.getValueAt(i, valueColumnIndex));
                        } else {
                            var rowData = model.getDataVector().get(i);
                            selected.add(rowData);
                        }
                    }
                } catch (IndexOutOfBoundsException ex) {
                    System.out.println("ButtonComponent error: " + ex.getMessage());
                }
            }

            if (JOptionPane.showConfirmDialog(null, "Deseja finalizar a seleção?", "Seleção de Deputados", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
                handleInfo.process(selected, proposal);

                if (jFrame != null) {
                    jFrame.dispose();
                }
            }

            selected.clear();
        });

        return new Button(button);
    }

    public static Button createSimpleButton(String label, ButtonAction btnAction) {
        JButton button = new JButton(label);
        button.addActionListener(btnAction::action);

        return new Button(button);
    }

}
