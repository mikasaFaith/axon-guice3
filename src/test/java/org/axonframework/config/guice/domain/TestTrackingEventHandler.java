package org.axonframework.config.guice.domain;

import org.axonframework.eventhandling.EventHandler;

public class TestTrackingEventHandler {

	@EventHandler
	public void on(TestAggregateCreatedEvent e) {
		System.out.println("Test Aggregated Created, Listened by Tracking Event Handler: " + e.getId());
	}
}
