package org.axonframework.config.guice;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactory;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.config.SagaConfiguration;
import org.axonframework.config.guice.provider.CommandBusProvider;
import org.axonframework.config.guice.provider.CommandGatewayFactoryProvider;
import org.axonframework.config.guice.provider.CommandGatewayProvider;
import org.axonframework.config.guice.provider.CustomCommandGatewayProvider;
import org.axonframework.config.guice.provider.EventBusProvider;
import org.axonframework.config.guice.provider.RepositoryProvider;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.saga.ResourceInjector;
import org.axonframework.eventhandling.saga.repository.SagaStore;
import org.axonframework.eventhandling.saga.repository.inmemory.InMemorySagaStore;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.util.Types;

public class AxonGuiceModule extends AbstractModule {

	final AxonConfig config;

	public AxonGuiceModule(AxonConfig config) {
		this.config = config;
	}

	@Override
	protected void configure() {

		Provider<Injector> injectorProvider = getProvider(Injector.class);
		Provider<CommandGatewayFactory> cmdGwFactoryProvider = getProvider(CommandGatewayFactory.class);
		Provider<Configuration> configProvider = getProvider(Configuration.class);
		Set<Provider<?>> cmdProviders = config.getCommandHandlerClasses().stream().map(cmd -> getProvider(cmd))
				.collect(Collectors.toSet());
		Set<Provider<?>> eventHandlerProviders = config.getEventHandlerClasses().stream().map(eh -> getProvider(eh))
				.collect(Collectors.toSet());
		Map<String, Set<Provider<?>>> trackingEventHandlerProviders = Maps.transformValues(
				config.getTrackingEventHandlerClasses(),
				(Set<Class<?>> s) -> s.stream().map(eh -> getProvider(eh)).collect(Collectors.toSet()));

		bind(Configuration.class).toProvider(() -> {
			Configurer conf = DefaultConfigurer.defaultConfiguration();

			// aggregates
			config.getAggregateClasses().forEach(aggCls -> conf.configureAggregate(aggCls));

			// command handlers
			cmdProviders.forEach(cmdProvider -> {
				conf.registerCommandHandler(ac -> cmdProvider.get());
			});

			// event handlers: subscribing and tracking
			eventHandlerProviders.forEach(ehProvider -> {
				// subscribing event handlers
				EventHandlingConfiguration ehConfiguration = new EventHandlingConfiguration()
						.registerEventHandler(ac -> ehProvider.get());
				conf.registerModule(ehConfiguration);
			});

			trackingEventHandlerProviders.forEach((name, ehProviders) -> {
				// tracking event handlers
				EventHandlingConfiguration ehConfiguration = new EventHandlingConfiguration();
				ehProviders.forEach(ehProvider -> {
					ehConfiguration.registerEventHandler(ac -> ehProvider.get());
				});
				ehConfiguration.byDefaultAssignTo(name).usingTrackingProcessors();
				conf.registerModule(ehConfiguration);
			});

			// saga: subscribing and tracking
			config.getSagaClasses().forEach(s -> {
				SagaConfiguration<?> sagaConfig = SagaConfiguration.subscribingSagaManager(s)
						.configureSagaStore(ac -> ac.getComponent(SagaStore.class));
				conf.registerModule(sagaConfig);
			});

			config.getTrackingSagaClasses().forEach(s -> {
				SagaConfiguration<?> sagaConfig = SagaConfiguration.trackingSagaManager(s)
						.configureSagaStore(ac -> ac.getComponent(SagaStore.class));
				conf.registerModule(sagaConfig);
			});

			// infrastructure definition
			conf.registerComponent(SagaStore.class, ac -> new InMemorySagaStore());
			conf.configureEmbeddedEventStore(ac -> new InMemoryEventStorageEngine());

			conf.configureResourceInjector(ac -> new ResourceInjector() {
				public void injectResources(Object saga) {
					injectorProvider.get().injectMembers(saga);
				}
			});

			Configuration configuration = conf.buildConfiguration();
			configuration.start();
			return configuration;
		}).in(Singleton.class);

		// guice infrastructure binding
		bind(CommandBus.class).toProvider(CommandBusProvider.class).in(Singleton.class);
		bind(EventBus.class).toProvider(EventBusProvider.class).in(Singleton.class);
		bind(CommandGateway.class).toProvider(CommandGatewayProvider.class).in(Singleton.class);

		// guice command gateway binding
		bind(CommandGatewayFactory.class).toProvider(CommandGatewayFactoryProvider.class).in(Singleton.class);
		config.getCommandGatewayClasses().forEach(cmdGwCls -> bind(cmdGwCls)
				.toProvider(new CustomCommandGatewayProvider(cmdGwCls, cmdGwFactoryProvider)));

		// guice repository binding
		config.getAggregateClasses()
				.forEach(aggCls -> bind(Key.get(Types.newParameterizedType(Repository.class, aggCls)))
						.toProvider(new RepositoryProvider(aggCls, configProvider)).in(Singleton.class));
	}

}
