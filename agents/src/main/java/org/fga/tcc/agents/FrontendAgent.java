package org.fga.tcc.agents;

import jade.core.Agent;
import org.fga.tcc.pages.HomePage;

import java.io.Serial;

public class FrontendAgent extends Agent {

    @Serial
    private static final long serialVersionUID = -2797189365541352024L;

    private final HomePage homePage = new HomePage();

    @Override
    protected void setup() {
        homePage
            .setButtonHandleInfo((data) -> {
                System.out.println("Sending data to agents: " + data);
            })
            .buildHomePage();
    }

}
