package org.axonframework.config.guice.provider;

import org.axonframework.commandhandling.model.Repository;
import org.axonframework.config.Configuration;

import com.google.inject.Provider;

public class RepositoryProvider<T> implements Provider<Repository<T>> {

	final Class<T> aggregateClass;
	final Provider<Configuration> axonConfigProvider;

	public RepositoryProvider(Class<T> aggregateClass, Provider<Configuration> axonConfig) {
		this.aggregateClass = aggregateClass;
		this.axonConfigProvider = axonConfig;
	}

	@Override
	public Repository<T> get() {
		return axonConfigProvider.get().repository(aggregateClass);
	}

}
