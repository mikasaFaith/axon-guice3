package org.axonframework.config.guice.provider;

import org.axonframework.config.Configuration;
import org.axonframework.eventhandling.EventBus;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class EventBusProvider implements Provider<EventBus> {

	@Inject
	Provider<Configuration> config;

	@Override
	public EventBus get() {
		return config.get().eventBus();
	}

}
