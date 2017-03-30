package org.axonframework.config.guice;

import org.axonframework.commandhandling.gateway.CommandGatewayFactory;
import org.axonframework.config.Configuration;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.config.SagaConfiguration;
import org.axonframework.config.guice.provider.CommandGatewayFactoryProvider;
import org.axonframework.config.guice.provider.CustomCommandGatewayProvider;
import org.axonframework.eventhandling.saga.repository.SagaStore;
import org.axonframework.eventhandling.saga.repository.inmemory.InMemorySagaStore;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.inmemory.InMemoryTokenStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.thoughtworks.xstream.XStream;

public class AxonGuiceModule extends AbstractModule {

	final AxonConfig config;

	public AxonGuiceModule(AxonConfig config) {
		this.config = config;
	}

	public GuiceConfigurer initConfigurer(AxonConfig config, Provider<Injector> injectorProvider) {

		GuiceConfigurer configurer = GuiceConfigurer.defaultConfiguration(binder());

		// infrastructure definition
		configurer.registerComponent(SagaStore.class, ac -> createSagaStore(ac, config, injectorProvider));
		configurer.registerComponent(TokenStore.class, ac -> createTokenStore(ac, config, injectorProvider));
		configurer.configureEmbeddedEventStore(ac -> createEventStorageEngine(ac, config, injectorProvider));

		// aggregates
		config.getAggregateClasses().forEach(agg -> configurer.configureAggregate(agg));

		// command handlers
		config.getCommandHandlerClasses().forEach(cmdCls -> {
			configurer.registerCommandHandler(ac -> injectorProvider.get().getInstance(cmdCls));
		});

		// event handlers: subscribing and tracking
		// subscribing event handlers
		final EventHandlingConfiguration subscribingEventHandlerCfg = new EventHandlingConfiguration();
		config.getEventHandlerClasses().forEach(ehCls -> {
			subscribingEventHandlerCfg.registerEventHandler(ac -> injectorProvider.get().getInstance(ehCls));
		});
		configurer.registerModule(subscribingEventHandlerCfg);

		// tracking event handlers
		config.getTrackingEventHandlerClasses().forEach((name, ehClasses) -> {
			EventHandlingConfiguration trackingEventHandlerCfg = new EventHandlingConfiguration();
			ehClasses.forEach(ehCls -> {
				trackingEventHandlerCfg.registerEventHandler(ac -> injectorProvider.get().getInstance(ehCls));
			});
			trackingEventHandlerCfg.byDefaultAssignTo(name).usingTrackingProcessors();
			configurer.registerModule(trackingEventHandlerCfg);
		});

		// saga: subscribing and tracking
		config.getSagaClasses().forEach(s -> {
			SagaConfiguration<?> sagaConfig = SagaConfiguration.subscribingSagaManager(s)
					.configureSagaStore(ac -> ac.getComponent(SagaStore.class));
			configurer.registerModule(sagaConfig);
		});
		config.getTrackingSagaClasses().forEach(s -> {
			SagaConfiguration<?> sagaConfig = SagaConfiguration.trackingSagaManager(s)
					.configureSagaStore(ac -> ac.getComponent(SagaStore.class));
			configurer.registerModule(sagaConfig);
		});
		return configurer;
	}

	@Override
	protected void configure() {

		Provider<Injector> injectorProvider = getProvider(Injector.class);
		bind(AxonConfig.class).toInstance(config);

		GuiceConfigurer configurer = initConfigurer(config, injectorProvider);

		bind(Configuration.class).toProvider(() -> {
			Configuration configuration = configurer.buildConfiguration();
			configuration.start();
			return configuration;
		}).asEagerSingleton();

		// guice command gateway binding
		Provider<CommandGatewayFactory> cmdGwFactoryProvider = getProvider(CommandGatewayFactory.class);
		bind(CommandGatewayFactory.class).toProvider(CommandGatewayFactoryProvider.class).in(Singleton.class);
		config.getCommandGatewayClasses().forEach(cmdGwCls -> bind(cmdGwCls)
				.toProvider(new CustomCommandGatewayProvider(cmdGwCls, cmdGwFactoryProvider)));
		configurer.bind();
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
