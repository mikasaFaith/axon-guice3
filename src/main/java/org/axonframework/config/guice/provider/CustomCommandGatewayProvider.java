package org.axonframework.config.guice.provider;

import org.axonframework.commandhandling.gateway.CommandGatewayFactory;

import com.google.inject.Provider;

public class CustomCommandGatewayProvider<T> implements Provider<T> {

	final Provider<CommandGatewayFactory> provider;

	final Class<T> gatewayClass;

	public CustomCommandGatewayProvider(Class<T> cmdGateway, Provider<CommandGatewayFactory> provider) {
		this.provider = provider;
		this.gatewayClass = cmdGateway;
	}

	@Override
	public T get() {
		return provider.get().createGateway(gatewayClass);
	}
}
