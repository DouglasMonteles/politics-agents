package org.fga.tcc;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import org.fga.tcc.agents.DeputyAgent;
import org.fga.tcc.agents.DeputyEnvironmentAgent;

public class MainContainer {

    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        ContainerController containerController = runtime.createMainContainer(profile);

        try {
            AgentController environmentAgent = containerController
                    .createNewAgent("EnvironmentAgent", DeputyEnvironmentAgent.class.getName(), null);
            AgentController deputyAgent = containerController
                    .createNewAgent("DeputyAgent", DeputyAgent.class.getName(), new Object[] { "Carlos", "PT" });

            AgentController rma = containerController.createNewAgent("rma", "jade.tools.rma.rma", null);

            environmentAgent.start();
            deputyAgent.start();
            rma.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
