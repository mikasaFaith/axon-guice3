package org.axonframework.config.guice.domain;

public class TestAggregateCreatedEvent {

	final String id;

	public TestAggregateCreatedEvent(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

}
