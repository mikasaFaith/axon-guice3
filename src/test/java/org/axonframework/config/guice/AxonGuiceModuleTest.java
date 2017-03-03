package org.axonframework.config.guice;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.guice.domain.CreateTestAggregateCommand;
import org.axonframework.config.guice.domain.GetCurrentDateCommand;
import org.axonframework.config.guice.domain.TestAggregate;
import org.axonframework.config.guice.domain.TestCommandHandler;
import org.axonframework.config.guice.domain.TestEventHandler;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class AxonGuiceModuleTest {

	@Test
	public void test() {
		AxonConfig config = AxonConfigBuilder.create() //
				.withAggregate(TestAggregate.class)
				.withCommandHandler(TestCommandHandler.class)
				.withEventHandler(TestEventHandler.class)
				.build();

		AxonGuiceModule module = new AxonGuiceModule(config);
		Injector i = Guice.createInjector(module);
		assertNotNull(i.getInstance(CommandGateway.class));
		CommandGateway gateway = i.getInstance(CommandGateway.class);
		Date date = gateway.sendAndWait(new GetCurrentDateCommand());
		System.out.println("Date: " + date);
		assertNotNull(date);

		String id = gateway.sendAndWait(new CreateTestAggregateCommand());
		System.out.println("Test Aggregate ID: " + id);
		assertNotNull(id);
	}

}
