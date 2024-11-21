package org.fga.tcc;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.fga.tcc.agents.DeputyAgent;
import org.fga.tcc.agents.DeputyEnvironmentAgent;
import org.fga.tcc.services.AgentService;
import org.fga.tcc.services.impl.AgentServiceImpl;

public class MainContainer {

    public static void main(String[] args) {
        AgentService agentService = AgentServiceImpl.getInstance();

        AgentController environmentAgent = agentService
                .createAgent("DeputyEnvironmentAgent", DeputyEnvironmentAgent.class.getName(), null);
//        AgentController deputyAgent = agentService
//                .createAgent("DeputyAgent", DeputyAgent.class.getName(), new Object[] { "Carlos", "PT" });

        AgentController rma = agentService.createRmaAgent(null);

        try {
            rma.start();
            environmentAgent.start();
//            deputyAgent.start();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
    }

}
