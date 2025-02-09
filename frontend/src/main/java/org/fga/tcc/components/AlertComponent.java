package org.fga.tcc.components;

import javax.swing.*;
import java.awt.*;

public class AlertComponent {

    public static String inputConfirmDialog(String label, String title) {
        JTextArea textField = new JTextArea(2, 30);
        JScrollPane jScrollPane = new JScrollPane(textField);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.NORTH);
        panel.add(jScrollPane, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String text = textField.getText();

            if ((text == null || text.isEmpty()) || text.length() < 10) {
                JOptionPane.showMessageDialog(null, "Informe um texto com mais de 10 caracteres", "Erro", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return text;
        }

        return null;
    }

}
