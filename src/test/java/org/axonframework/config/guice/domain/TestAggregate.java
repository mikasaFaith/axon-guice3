package org.axonframework.config.guice.domain;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;

public class TestAggregate {

	@AggregateIdentifier
	String id;
	
	TestAggregate() {
	}

	@CommandHandler
	public TestAggregate(CreateTestAggregateCommand cmd) {
		apply(new TestAggregateCreatedEvent(UUID.randomUUID().toString()));
	}

	@EventSourcingHandler
	protected void on(TestAggregateCreatedEvent ev) {
		this.id = ev.getId();
	}
}
