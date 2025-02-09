package org.fga.tcc.pages;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Setter;
import org.fga.tcc.components.ButtonComponent;
import org.fga.tcc.components.HandleActionButton;
import org.fga.tcc.components.TableComponent;
import org.fga.tcc.entities.NominalVote;
import org.fga.tcc.entities.OpenDataBaseResponseList;
import org.fga.tcc.entities.Voting;
import org.fga.tcc.models.Button;
import org.fga.tcc.models.Table;
import org.fga.tcc.services.VotingService;
import org.fga.tcc.services.impl.VotingServiceImpl;
import org.fga.tcc.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Setter
public class SelectVotingPage extends JFrame {

    private HandleActionButton buttonHandleInfo;

    public SelectVotingPage setButtonHandleInfo(HandleActionButton buttonHandleInfo) {
        this.buttonHandleInfo = buttonHandleInfo;
        return this;
    }


    public void buildPage() {
        VotingService voteService = new VotingServiceImpl();
        Object[] columnData = new Object[]{ "Id Votação", "Desc. da Proposição", "Qtd. Deputados(as)", "Seleção"};
        List<Object[]> rowData = new ArrayList<>();

        String nominalVotesPath = "external-data-processor/src/main/resources/votacoes/votos";

        FileUtils.readFile(nominalVotesPath, (path, mapper) -> {
            String votingId = path.getFileName().toString().replace(".json", "");
            Voting voting = voteService.getVotingById(votingId);

            OpenDataBaseResponseList<NominalVote> openDataBaseResponse = mapper.readValue(path.toFile(), new TypeReference<>() {});

            if (voting.getProposal().getDescription() != null && !voting.getProposal().getDescription().isEmpty()) {
                rowData.add(new Object[] {
                        votingId,
                        voting.getProposal().getDescription(),
                        openDataBaseResponse.getData().size(),
                        false
                });
            }
        });

        setTitle("Simulação de Voto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        Table table = TableComponent.createTableVoteWithCheckbox(
                columnData,
                rowData.toArray(Object[][]::new)
        );

        Button button = ButtonComponent.createTableButton("Finalizar Seleção", table.getModel(), 0, this, this.buttonHandleInfo, null);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.add(new JScrollPane(table.getTable()));

        JPanel optionButtons = new JPanel();
        optionButtons.add(button.getButton());

        add(mainContent, BorderLayout.CENTER);
        add(optionButtons, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
