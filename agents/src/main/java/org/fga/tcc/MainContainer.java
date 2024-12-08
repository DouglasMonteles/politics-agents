package org.fga.tcc;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.fga.tcc.agents.DeputyAgent;
import org.fga.tcc.agents.DeputyManagerAgent;
import org.fga.tcc.services.AgentService;
import org.fga.tcc.services.impl.AgentServiceImpl;

public class MainContainer {

    public static void main(String[] args) {
        AgentService agentService = AgentServiceImpl.getInstance();

        AgentController deputyEnvironmentAgent = agentService
                .createAgent("DeputyEnvironmentAgent", DeputyManagerAgent.class.getName(), null);
        AgentController rma = agentService.createRmaAgent(null);

        try {
            rma.start();
            deputyEnvironmentAgent.start();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
    }

}
