package org.fga.tcc.pages;

import lombok.Setter;
import org.fga.tcc.components.ButtonComponent;
import org.fga.tcc.components.ButtonHandleInfo;
import org.fga.tcc.components.TableComponent;
import org.fga.tcc.entities.Deputy;
import org.fga.tcc.entities.Party;
import org.fga.tcc.models.Button;
import org.fga.tcc.models.Table;
import org.fga.tcc.services.DeputyService;
import org.fga.tcc.services.PartyService;
import org.fga.tcc.services.impl.DeputyServiceImpl;
import org.fga.tcc.services.impl.PartyServiceImpl;

import javax.swing.*;
import java.awt.*;

@Setter
public class SelectAcronymPartyPage extends JFrame {

    private ButtonHandleInfo buttonHandleInfo;

    public SelectAcronymPartyPage setButtonHandleInfo(ButtonHandleInfo buttonHandleInfo) {
        this.buttonHandleInfo = buttonHandleInfo;
        return this;
    }

    public void buildPartyPage() {
        PartyService partyService = PartyServiceImpl.getInstance();
        var parties = partyService.getParties();

        Object[] columnData = new Object[]{ "ID", "Sigla", "Nome", "Seleção"};
        Object[][] rowData = new Object[parties.size()][4];

        for (int i = 0; i < parties.size(); i++) {
            Party party = parties.get(i);
            rowData[i] = new Object[] {
                party.getId(),
                party.getName(),
                party.getAcronym(),
                false,
            };
        }

        setTitle("Partidos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        Table table = TableComponent.createTableWithCheckbox(
                columnData,
                rowData
        );

        Button button = ButtonComponent.createTableButton("Finalizar Seleção", table.getModel(), 1, this.buttonHandleInfo);

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
