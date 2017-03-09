package org.axonframework.config.guice;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactory;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.config.AggregateConfigurer;
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
import org.axonframework.eventhandling.saga.repository.SagaStore;
import org.axonframework.eventhandling.saga.repository.inmemory.InMemorySagaStore;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.inmemory.InMemoryTokenStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.util.Types;
import com.thoughtworks.xstream.XStream;

public class AxonGuiceModule extends AbstractModule {

	final AxonConfig config;

	public AxonGuiceModule(AxonConfig config) {
		this.config = config;
	}

	@Override
	protected void configure() {

		Provider<Injector> injectorProvider = getProvider(Injector.class);
		Provider<CommandGatewayFactory> cmdGwFactoryProvider = getProvider(CommandGatewayFactory.class);
		Set<Provider<?>> cmdProviders = config.getCommandHandlerClasses().stream().map(cmd -> getProvider(cmd))
				.collect(Collectors.toSet());
		Set<Provider<?>> eventHandlerProviders = config.getEventHandlerClasses().stream().map(eh -> getProvider(eh))
				.collect(Collectors.toSet());
		Map<String, Set<Provider<?>>> trackingEventHandlerProviders = Maps
				.newHashMap(Maps.transformValues(config.getTrackingEventHandlerClasses(),
						(Set<Class<?>> s) -> s.stream().map(eh -> getProvider(eh)).collect(Collectors.toSet())));
		Set<AggregateConfigurer<?>> aggConf = config.getAggregateClasses().stream()
				.map(agg -> AggregateConfigurer.defaultConfiguration(agg)).collect(Collectors.toSet());

		bind(AxonConfig.class).toInstance(config);
		bind(Configuration.class).toProvider(() -> {
			Configurer conf = DefaultConfigurer.defaultConfiguration();

			// aggregates
			aggConf.forEach(agg -> conf.configureAggregate(agg));

			// command handlers
			cmdProviders.forEach(cmdProvider -> {
				conf.registerCommandHandler(ac -> cmdProvider.get());
			});

			// event handlers: subscribing and tracking
			final EventHandlingConfiguration subscribingEventHandlerCfg = new EventHandlingConfiguration();
			eventHandlerProviders.forEach(ehProvider -> {
				// subscribing event handlers
				subscribingEventHandlerCfg.registerEventHandler(ac -> ehProvider.get());
			});
			conf.registerModule(subscribingEventHandlerCfg);

			trackingEventHandlerProviders.forEach((name, ehProviders) -> {
				// tracking event handlers
				EventHandlingConfiguration trackingEventHandlerCfg = new EventHandlingConfiguration();
				ehProviders.forEach(ehProvider -> {
					trackingEventHandlerCfg.registerEventHandler(ac -> ehProvider.get());
				});
				trackingEventHandlerCfg.byDefaultAssignTo(name).usingTrackingProcessors();
				conf.registerModule(trackingEventHandlerCfg);
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
			conf.registerComponent(SagaStore.class, ac -> createSagaStore(ac, config, injectorProvider));
			conf.registerComponent(TokenStore.class, ac -> createTokenStore(ac, config, injectorProvider));
			conf.configureEmbeddedEventStore(ac -> createEventStorageEngine(ac, config, injectorProvider));

			conf.configureResourceInjector(ac -> new GuiceResourceInjector(injectorProvider));

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
		aggConf.forEach(aggCls -> {
			bind(Key.get(Types.newParameterizedType(Repository.class, aggCls.aggregateType()))) //
					.toProvider(new RepositoryProvider(aggCls)) //
					.in(Singleton.class);
		});
	}

	protected SagaStore<Object> createSagaStore(Configuration conf, AxonConfig config,
			Provider<Injector> injectorProvider) {
		return new InMemorySagaStore();
	}

	protected TokenStore createTokenStore(Configuration conf, AxonConfig config, Provider<Injector> injectorProvider) {
		return new InMemoryTokenStore();
	}

	protected EventStorageEngine createEventStorageEngine(Configuration conf, AxonConfig config,
			Provider<Injector> injectorProvider) {
		// USE the Serializer for persistence storage
		// Serializer serializer = createSerializer(conf, config,
		// injectorProvider);
		return new InMemoryEventStorageEngine();
	}

	protected Serializer createSerializer(Configuration conf, AxonConfig config, Provider<Injector> injectorProvider) {
		XStreamSerializer eventSerializer = new XStreamSerializer();
		XStream xStream = eventSerializer.getXStream();
		config.getConverterClasses().forEach(cc -> {
			xStream.registerConverter(injectorProvider.get().getInstance(cc));
		});
		return eventSerializer;
	}

}
