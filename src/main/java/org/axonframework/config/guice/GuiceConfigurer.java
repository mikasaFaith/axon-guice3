package org.axonframework.config.guice;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.config.AggregateConfiguration;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.guice.provider.RepositoryProvider;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.saga.ResourceInjector;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.serialization.Serializer;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.util.Types;

public class GuiceConfigurer extends DefaultConfigurer {

	final Binder binder;
	final Provider<Injector> injectorProvider;
	final Map<Class<Object>, Provider<Object>> readyToBindComponents = new HashMap<>();

	//
	ResourceInjector resourceInjectorInstance;
	ParameterResolverFactory parameterResolverFactoryInstance;
	Serializer serializerInstance;
	CommandBus commandBusInstance;
	EventBus eventBusInstance;
	CommandGateway commandGatewayInstance;

	protected GuiceConfigurer(Binder binder) {
		this.binder = binder;
		this.injectorProvider = binder.getProvider(Injector.class);
		registerComponent(ParameterResolverFactory.class, this::doGetDefaultParameterResolverFactory);
		registerComponent(Serializer.class, this::doGetDefaultSerializer);
		registerComponent(CommandBus.class, this::doGetDefaultCommandBus);
		registerComponent(EventBus.class, this::doGetDefaultEventBus);
		registerComponent(CommandGateway.class, this::doGetDefaultCommandGateway);
		registerComponent(ResourceInjector.class, this::doGetDefaultResourceInjector);
	}

	public static GuiceConfigurer defaultConfiguration(Binder binder) {
		return new GuiceConfigurer(binder);
	}

	@Override
	protected ResourceInjector defaultResourceInjector(Configuration config) {
		return new GuiceResourceInjector(injectorProvider);
	}

	protected ParameterResolverFactory doGetDefaultParameterResolverFactory(Configuration config) {
		if (parameterResolverFactoryInstance == null) {
			parameterResolverFactoryInstance = defaultParameterResolverFactory(config);
		}
		return parameterResolverFactoryInstance;
	}

	protected EventBus doGetDefaultEventBus(Configuration config) {
		if (eventBusInstance == null) {
			eventBusInstance = defaultEventBus(config);
		}
		return eventBusInstance;
	}

	protected CommandBus doGetDefaultCommandBus(Configuration config) {
		if (commandBusInstance == null) {
			commandBusInstance = defaultCommandBus(config);
		}
		System.out.println("CommandBus: " + commandBusInstance);
		return commandBusInstance;
	}

	protected CommandGateway doGetDefaultCommandGateway(Configuration config) {
		if (commandGatewayInstance == null) {
			commandGatewayInstance = defaultCommandGateway(config);
		}
		System.out.println("CommandGateway: " + commandGatewayInstance);
		DefaultCommandGateway gw = (DefaultCommandGateway) commandGatewayInstance;
		System.out.println("CommandBus in Gateway: " + gw.getCommandBus());
		return commandGatewayInstance;
	}

	protected Serializer doGetDefaultSerializer(Configuration config) {
		if (serializerInstance == null) {
			serializerInstance = defaultSerializer(config);
		}
		return serializerInstance;
	}

	protected ResourceInjector doGetDefaultResourceInjector(Configuration config) {
		if (resourceInjectorInstance == null) {
			resourceInjectorInstance = defaultResourceInjector(config);
		}
		return resourceInjectorInstance;
	}

	@Override
	public <C> Configurer registerComponent(Class<C> componentType,
			Function<Configuration, ? extends C> componentBuilder) {
		super.registerComponent(componentType, componentBuilder);
		readyToBindComponents.put((Class<Object>) componentType, () -> componentBuilder.apply(getConfig()));
		return this;
	}

	@Override
	public <A> Configurer configureAggregate(AggregateConfiguration<A> aggregateConfiguration) {
		super.configureAggregate(aggregateConfiguration);
		// guice repository binding
		binder.bind(Key.get(Types.newParameterizedType(Repository.class, aggregateConfiguration.aggregateType()))) //
				.toProvider(new RepositoryProvider(aggregateConfiguration)) //
				.in(Singleton.class);
		return this;
	}

	public void bind() {
		readyToBindComponents.forEach((c, p) -> {
			binder.bind(c).toProvider(p).in(Singleton.class);
		});
	}
}
