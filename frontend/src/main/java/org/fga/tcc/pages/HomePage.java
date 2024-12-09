package org.fga.tcc.pages;

import org.fga.tcc.components.ButtonComponent;
import org.fga.tcc.components.ButtonHandleInfo;
import org.fga.tcc.models.Button;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JFrame {

    private ButtonHandleInfo buttonHandleInfo;

    public HomePage setButtonHandleInfo(ButtonHandleInfo buttonHandleInfo) {
        this.buttonHandleInfo = buttonHandleInfo;
        return this;
    }

    public void buildHomePage() {
        setTitle("Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        Button deputyBtn = ButtonComponent.createSimpleButton("Deputados", (e) -> {
            new SelectDeputyPage()
                    .setButtonHandleInfo(buttonHandleInfo)
                    .buildDeputyPage();
        });

        Button partyAcronymBtn = ButtonComponent.createSimpleButton("Partidos", (e) -> {
            new SelectAcronymPartyPage()
                    .setButtonHandleInfo(buttonHandleInfo)
                    .buildPartyPage();
        });

        JPanel sideMenu = new JPanel(new GridLayout(16, 1));
        sideMenu.add(deputyBtn.getButton());
        sideMenu.add(partyAcronymBtn.getButton());

        add(sideMenu, BorderLayout.WEST);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
