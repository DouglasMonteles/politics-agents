package org.fga.tcc.agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import org.fga.tcc.pages.HomePage;
import org.fga.tcc.pages.VotingProcessPage;

import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;

public class FrontendAgent extends Agent {

    @Serial
    private static final long serialVersionUID = -2797189365541352024L;

    private final HomePage homePage = new HomePage();

    @Override
    protected void setup() {
        homePage
            .setButtonHandleInfo((data) -> {
                try {
                    System.out.println("Sending list of agents ids: " + data);

                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.addReceiver(getAID("DeputyEnvironmentAgent"));
                    message.setContentObject((ArrayList<Object>) data);

                    send(message);

                    new VotingProcessPage()
                            .buildVotingProcessPage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .buildHomePage();
    }

}
