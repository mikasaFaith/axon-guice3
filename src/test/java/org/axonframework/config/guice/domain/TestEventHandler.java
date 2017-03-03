package org.axonframework.config.guice.domain;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;

import com.google.inject.Inject;

public class TestEventHandler {

	@Inject
	CommandGateway gateway;
	
	@EventHandler
	public void on(TestAggregateCreatedEvent e) {
		System.out.println("Test Aggregated Created, Listened by Event Handler: "+ e.getId());
		System.out.println("Command Gateway: "+ gateway);
	}
}
