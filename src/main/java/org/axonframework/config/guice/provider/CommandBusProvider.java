package org.axonframework.config.guice.provider;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.Configuration;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class CommandBusProvider implements Provider<CommandBus> {

	@Inject
	Provider<Configuration> config;

	@Override
	public CommandBus get() {
		return config.get().commandBus();
	}

}
