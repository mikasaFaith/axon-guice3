package org.axonframework.config.guice.domain;

import java.util.List;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TestEventHandler {

	@Inject
	CommandGateway gateway;

	List<Object> eventReceived = Lists.newArrayList();

	@EventHandler
	public void on(TestAggregateCreatedEvent e) {
		System.out.println("Test Aggregate Created, Listened by Event Handler: " + e.getId());
		System.out.println("Command Gateway: " + gateway);
		eventReceived.add(e);
	}

	public CommandGateway getGateway() {
		return gateway;
	}

	public List<Object> getEventReceived() {
		return eventReceived;
	}

}
