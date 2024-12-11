package org.fga.tcc.agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import org.fga.tcc.entities.Deputy;
import org.fga.tcc.pages.HomePage;
import org.fga.tcc.pages.VotingProcessPage;
import org.fga.tcc.services.DeputyService;
import org.fga.tcc.services.impl.DeputyServiceImpl;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FrontendAgent extends Agent {

    @Serial
    private static final long serialVersionUID = -2797189365541352024L;

    private final HomePage homePage = new HomePage();

    private final DeputyService deputyService = DeputyServiceImpl.getInstance();

    @Override
    protected void setup() {
        homePage
            .setButtonHandleInfo((data) -> {
                try {
                    System.out.println("Sending list of agents ids: " + data);

                    List<Deputy> deputies = deputyService.getDeputes()
                        .stream()
                        .filter(deputy -> data.contains(deputy.getName()) || data.contains(deputy.getPartyAcronym()))
                        .toList();

                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    message.addReceiver(getAID("DeputyEnvironmentAgent"));
                    message.setContentObject(new ArrayList<>(deputies));

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
