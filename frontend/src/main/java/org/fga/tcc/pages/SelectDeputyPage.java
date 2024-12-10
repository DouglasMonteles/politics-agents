package org.fga.tcc.pages;

import lombok.Setter;
import org.fga.tcc.components.ButtonComponent;
import org.fga.tcc.components.ButtonHandleInfo;
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

    private ButtonHandleInfo buttonHandleInfo;

    public SelectDeputyPage setButtonHandleInfo(ButtonHandleInfo buttonHandleInfo) {
        this.buttonHandleInfo = buttonHandleInfo;
        return this;
    }

    public void buildDeputyPage() {
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

        Button button = ButtonComponent.createTableButton("Finalizar Seleção", table.getModel(), 1, this, this.buttonHandleInfo);

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
