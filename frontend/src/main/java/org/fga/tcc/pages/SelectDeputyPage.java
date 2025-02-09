package org.fga.tcc.pages;

import lombok.Setter;
import org.fga.tcc.components.ButtonComponent;
import org.fga.tcc.components.HandleActionButton;
import org.fga.tcc.components.TableComponent;
import org.fga.tcc.entities.Deputy;
import org.fga.tcc.models.Button;
import org.fga.tcc.models.Table;
import org.fga.tcc.services.DeputyService;
import org.fga.tcc.services.impl.DeputyServiceImpl;

import javax.swing.*;
import java.awt.*;

@Setter
public class SelectDeputyPage extends JFrame {

    private HandleActionButton buttonHandleInfo;
    private String proposalText;

    public SelectDeputyPage setButtonHandleInfo(HandleActionButton buttonHandleInfo) {
        this.buttonHandleInfo = buttonHandleInfo;
        return this;
    }

    public SelectDeputyPage setProposalText(String proposalText) {
        this.proposalText = proposalText;
        return this;
    }

    public void buildPage() {
        DeputyService deputyService = DeputyServiceImpl.getInstance();
        var deputes = deputyService.getDeputes();
        Object[] columnData = new Object[]{ "ID", "Nome", "Partido", "Seleção"};
        Object[][] rowData = new Object[deputes.size()][4];

        for (int i = 0; i < deputes.size(); i++) {
            Deputy deputy = deputes.get(i);
            rowData[i] = new Object[] {
                deputy.getId(),
                deputy.getName(),
                deputy.getPartyAcronym(),
                false,
            };
        }

        setTitle("Deputados");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        Table table = TableComponent.createTableWithCheckbox(
                columnData,
                rowData
        );

        Button button = ButtonComponent.createTableButton("Finalizar Seleção", table.getModel(), 1, this, this.buttonHandleInfo, this.proposalText);

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
