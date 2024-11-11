package org.fga.tcc;

import jade.core.Agent;

import java.util.Iterator;

public class HelloWorldAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("Hello World.");

        System.out.println("Todas as minhas informações: \n");
        System.out.println("Id do agente: " + getAID());
        System.out.println("Meu nome local é: " + getAID().getLocalName());
        System.out.println("Meu nome global (GUID) é: " + getAID().getName());

        System.out.println("Meus endereços são:");
        Iterator it = getAID().getAllAddresses();
        while (it.hasNext()) {
            System.out.println("- " + it.next());
        }
    }

}