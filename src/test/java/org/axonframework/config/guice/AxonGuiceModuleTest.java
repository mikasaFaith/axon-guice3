package org.axonframework.config.guice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.guice.domain.CreateTestAggregateCommand;
import org.axonframework.config.guice.domain.GetCurrentDateCommand;
import org.axonframework.config.guice.domain.TestAggregate;
import org.axonframework.config.guice.domain.TestAggregateCreatedEvent;
import org.axonframework.config.guice.domain.TestCommandHandler;
import org.axonframework.config.guice.domain.TestEventHandler;
import org.axonframework.config.guice.domain.TestSaga;
import org.axonframework.config.guice.domain.TestTrackingEventHandler;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class AxonGuiceModuleTest {

	@Test
	public void test() throws Exception {
		AxonConfig config = AxonConfigBuilder.create() //
				.withAggregate(TestAggregate.class) //
				.withCommandHandler(TestCommandHandler.class) //
				.withEventHandler(TestEventHandler.class) //
				.withTrackingEventHandler("test", TestTrackingEventHandler.class) //
				.withSaga(TestSaga.class) //
				.build();

		AxonGuiceModule module = new AxonGuiceModule(config);
		Injector i = Guice.createInjector(module);

		// check infrastructure is setup
		assertNotNull(i.getInstance(CommandGateway.class));
		CommandGateway gateway = i.getInstance(CommandGateway.class);
		Date date = gateway.sendAndWait(new GetCurrentDateCommand());
		System.out.println("Date: " + date);
		assertNotNull(date);

		// check if aggregate creation command works
		String id = gateway.sendAndWait(new CreateTestAggregateCommand());
		System.out.println("Test Aggregate ID: " + id);
		assertNotNull(id);

		// check if repository injection works
		TestClassInjection tj = i.getInstance(TestClassInjection.class);
		System.out.println("Test Aggregate Repo: " + tj.getRepo());
		assertNotNull(tj.getRepo());

		// test aggregate loading
		TestAggregate ta = tj.load(id);
		System.out.println("Test Aggregate Loaded, ID: " + ta.getId());
		assertNotNull(ta);

		// check if event is received, and injection works in event handler
		TestEventHandler eh = i.getInstance(TestEventHandler.class);
		assertEquals(1, eh.getEventReceived().size());
		assertEquals(TestAggregateCreatedEvent.class, eh.getEventReceived().get(0).getClass());
		assertNotNull(eh.getGateway());

	}

}
