package org.axonframework.config.guice.provider;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.Configuration;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class CommandGatewayProvider implements Provider<CommandGateway> {

	@Inject
	Provider<Configuration> config;

	@Override
	public CommandGateway get() {
		return config.get().commandGateway();
	}

}
