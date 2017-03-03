package org.axonframework.config.guice.provider;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGatewayFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class CommandGatewayFactoryProvider implements Provider<CommandGatewayFactory> {

	@Inject
	Provider<CommandBus> commandBus;

	@Override
	public CommandGatewayFactory get() {
		return new CommandGatewayFactory(commandBus.get());
	}

}
