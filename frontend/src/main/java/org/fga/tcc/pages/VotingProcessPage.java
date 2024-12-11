package org.fga.tcc.pages;

import org.fga.tcc.components.TableComponent;
import org.fga.tcc.models.Table;

import javax.swing.*;
import java.awt.*;

public class VotingProcessPage extends JFrame {

    public void buildVotingProcessPage() {
        setTitle("Processo de Votação");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        Table table = TableComponent.createTableWithVotingResult(
                new Object[] { "Agente", "Deputado(a)", "Voto" },
                new Object[][] {
                        { "Agente007", "Fulano de Tal", "✔" },
                        { "Agente008", "Sincrano de Tal", "❌" },
                }
        );

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.add(new JScrollPane(table.getTable()));

        JPanel headerContent = new JPanel();
        headerContent.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel pageTitle = new JLabel("Votos obtidos", SwingConstants.CENTER);
        pageTitle.setFont(new Font("Arial", Font.BOLD, 16));
        pageTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerContent.add(pageTitle);

        add(headerContent, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
