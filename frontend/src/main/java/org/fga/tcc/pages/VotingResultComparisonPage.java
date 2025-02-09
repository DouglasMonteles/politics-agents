package org.fga.tcc.pages;

import org.fga.tcc.components.TableComponent;
import org.fga.tcc.entities.Deputy;
import org.fga.tcc.entities.NominalVote;
import org.fga.tcc.models.Table;
import org.fga.tcc.observables.Voting;
import org.fga.tcc.observables.VotingObserver;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VotingResultComparisonPage extends JFrame implements VotingObserver {

    private final List<NominalVote> nominalVotes = new ArrayList<>();

    private final Voting voting = Voting.getInstance();

    private Table tableVotes = null;

    private Table tableResume = null;

    private Table tableRate = null;

    private String proposal;

    public VotingResultComparisonPage setNominalVotes(List<NominalVote> nominalVotes) {
        this.nominalVotes.clear();
        this.nominalVotes.addAll(nominalVotes);
        return this;
    }

    public VotingResultComparisonPage setProposal(String proposal) {
        this.proposal = proposal;
        return this;
    }

    public void buildPage() {
        voting.addObserver(this);

        setTitle("Processo de Votação - Comparação entre o obtido e o real");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JPanel headerContent = new JPanel();
        headerContent.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel pageTitle = new JLabel("Votos obtidos", SwingConstants.CENTER);
        pageTitle.setFont(new Font("Arial", Font.BOLD, 16));
        pageTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerContent.add(pageTitle);

        tableVotes = TableComponent.createTableWithVotingResult(
                new Object[] { "Id", "Deputado(a)", "Voto Obtido do Modelo", "Voto real" },
                new Object[][] {}
        );

        tableResume = TableComponent.createTable(
                new Object[] { "Total Favor (Agentes)", "Total Favor (Esperados)", "Total Contra (Agentes)", "Total Contra (Esperados)" },
                new Object[][] {}
        );

        tableRate = TableComponent.createTable(
                new Object[] { "Taxa de Acerto a Favor (%)", "Taxa de Erro Favor (%)", "Taxa de Acerto Contra (%)", "Taxa de Erro Contra (%)" },
                new Object[][] {}
        );

        var proposalLabel = new Label("Proposição:");
        proposalLabel.setFont(new Font("Arial", Font.BOLD, 12));

        var proposalText = new JTextArea(proposal);
        proposalText.setLineWrap(true);
        proposalText.setEditable(false);
        proposalText.setMargin(new Insets(0, 10, 20, 10));

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.add(proposalLabel);
        mainContent.add(proposalText);
        mainContent.add(new JScrollPane(tableVotes.getTable()));
        mainContent.add(new Label(""));
        mainContent.add(new JScrollPane(tableResume.getTable()));
        mainContent.add(new Label(""));
        mainContent.add(new JScrollPane(tableRate.getTable()));

        add(headerContent, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void update() {
        System.out.println("Votos: " + voting.getVotes());
        // Clear table rows
        tableVotes.getModel().setRowCount(0);
        tableResume.getModel().setRowCount(0);
        tableRate.getModel().setRowCount(0);

        for (Map.Entry<Integer, Integer> vote : voting.getVotes().entrySet()) {
            List<NominalVote> nominalVotesFiltered = nominalVotes
                    .stream()
                    .filter(it -> it.getDeputy().getId().equals(vote.getKey()))
                    .toList();

            if (!nominalVotesFiltered.isEmpty()) {
                Deputy deputy = nominalVotesFiltered.get(0).getDeputy();

                if (deputy != null && deputy.getId() != null && deputy.getName() != null && vote.getValue() != null) {
                    boolean isPredictedVoteFavor = vote.getValue().equals(1);
                    boolean isExpectedVoteFavor = nominalVotesFiltered.get(0).getVote().equalsIgnoreCase("sim");

                    tableVotes.getModel().addRow(new Object[] {
                            deputy.getId(),
                            deputy.getName(),
                            isPredictedVoteFavor ? "✔" : "X",
                            isExpectedVoteFavor ? "✔" : "X"
                    });
                }
            }
        }

        long favorVotesPredicted = voting.getVotes().values().stream().filter(it -> it == 1).count();
        long againstVotesPredicted = voting.getVotes().values().stream().filter(it -> it == 0).count();

        long favorVotesExpected = nominalVotes.stream().filter(it -> it.getVote().equalsIgnoreCase("sim")).count();
        long againstVotesExpected = nominalVotes.stream().filter(it -> it.getVote().equalsIgnoreCase("não")).count();

        long totalFavorAssertivePredictions = Math.min(favorVotesPredicted, favorVotesExpected);
        long totalFavorErrorPredictions = Math.abs(favorVotesExpected - favorVotesPredicted);

        float favorAssertiveRate = ((float) totalFavorAssertivePredictions / (totalFavorAssertivePredictions + totalFavorErrorPredictions)) * 100;
        float favorErrorRate = ((float) totalFavorErrorPredictions / (totalFavorAssertivePredictions + totalFavorErrorPredictions)) * 100;

        long totalAgainstAssertivePredictions = Math.min(againstVotesPredicted, againstVotesExpected);
        long totalAgainstErrorPredictions = Math.abs(againstVotesExpected - againstVotesPredicted);

        float againstAssertiveRate = ((float) totalAgainstAssertivePredictions / (totalAgainstAssertivePredictions + totalAgainstErrorPredictions)) * 100;
        float againstErrorRate = ((float) totalAgainstErrorPredictions / (totalAgainstAssertivePredictions + totalAgainstErrorPredictions)) * 100;

        tableResume.getModel().addRow(new Object[] {
                favorVotesPredicted,
                favorVotesExpected,
                againstVotesPredicted,
                againstVotesExpected,
        });

        tableRate.getModel().addRow(new Object[] {
                favorAssertiveRate,
                favorErrorRate,
                againstAssertiveRate,
                againstErrorRate,
        });
    }

}
