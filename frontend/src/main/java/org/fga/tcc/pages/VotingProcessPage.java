package org.fga.tcc.pages;

import org.fga.tcc.components.TableComponent;
import org.fga.tcc.entities.Deputy;
import org.fga.tcc.models.Table;
import org.fga.tcc.observables.Voting;
import org.fga.tcc.observables.VotingObserver;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VotingProcessPage extends JFrame implements VotingObserver {

    private List<Deputy> deputies = new ArrayList<>();

    private Voting voting = Voting.getInstance();

    private Table table = null;

    public VotingProcessPage setDeputies(List<Deputy> deputies) {
        this.deputies.clear();
        this.deputies.addAll(deputies);
        return this;
    }

    public void buildVotingProcessPage() {
        voting.addObserver(this);

        setTitle("Processo de Votação");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JPanel headerContent = new JPanel();
        headerContent.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel pageTitle = new JLabel("Votos obtidos", SwingConstants.CENTER);
        pageTitle.setFont(new Font("Arial", Font.BOLD, 16));
        pageTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerContent.add(pageTitle);

        table = TableComponent.createTableWithVotingResult(
                new Object[] { "Id", "Deputado(a)", "Voto" },
                new Object[][] {}
        );

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.add(new JScrollPane(table.getTable()));

        add(headerContent, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void update() {
        System.out.println("Votos: " + voting.getVotes());
        table.getModel().setRowCount(0);

        for (Map.Entry<Integer, Integer> vote : voting.getVotes().entrySet()) {
            Deputy deputy = deputies
                    .stream()
                    .filter(it -> it.getId().equals(vote.getKey()))
                    .toList()
                    .get(0);

            table.getModel().addRow(new Object[] {
                    deputy.getId(),
                    deputy.getName(),
                    vote.getValue().equals(1) ? "✔" : "❌",
            });
        }
    }

    private void handleTable(Object[][] rows) {

    }

}
