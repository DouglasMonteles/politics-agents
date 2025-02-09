package org.fga.tcc.pages;

import org.fga.tcc.components.AlertComponent;
import org.fga.tcc.components.ButtonComponent;
import org.fga.tcc.components.HandleActionButton;
import org.fga.tcc.models.Button;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JFrame {

    private HandleActionButton buttonHandleInfo;
    private HandleActionButton buttonHandleVoteSimulation;

    public HomePage setButtonHandleInfo(HandleActionButton buttonHandleInfo) {
        this.buttonHandleInfo = buttonHandleInfo;
        return this;
    }

    public HomePage setButtonVoteSimulation(HandleActionButton buttonHandleVoteSimulation) {
        this.buttonHandleVoteSimulation = buttonHandleVoteSimulation;
        return this;
    }

    public void buildPage() {
        setTitle("Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        Button deputyBtn = ButtonComponent.createSimpleButton("Deputados", (e) -> {
            String result = AlertComponent.inputConfirmDialog("Informe o texto da Proposição", "Proposição");

            if (result != null) {
                new SelectDeputyPage()
                        .setButtonHandleInfo(buttonHandleInfo)
                        .setProposalText(result)
                        .buildPage();
            }
        });

        Button partyAcronymBtn = ButtonComponent.createSimpleButton("Partidos", (e) -> {
            String result = AlertComponent.inputConfirmDialog("Informe o texto da Proposição", "Proposição");

            if (result != null) {
                new SelectAcronymPartyPage()
                        .setButtonHandleInfo(buttonHandleInfo)
                        .setProposalText(result)
                        .buildPage();
            }
        });

        Button voteSimulationBtn = ButtonComponent.createSimpleButton("Simulação de Votação", (e) -> {
            new SelectVotingPage()
                    .setButtonHandleInfo(buttonHandleVoteSimulation)
                    .buildPage();
        });

        JPanel sideMenu = new JPanel(new GridLayout(16, 1));
        sideMenu.add(deputyBtn.getButton());
        sideMenu.add(partyAcronymBtn.getButton());
        sideMenu.add(voteSimulationBtn.getButton());

        add(sideMenu, BorderLayout.WEST);
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
