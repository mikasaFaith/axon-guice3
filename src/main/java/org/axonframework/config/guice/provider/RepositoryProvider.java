package org.axonframework.config.guice.provider;

import org.axonframework.commandhandling.model.Repository;
import org.axonframework.config.AggregateConfigurer;

import com.google.inject.Provider;

public class RepositoryProvider<T> implements Provider<Repository<T>> {

	final AggregateConfigurer<T> aggregateClass;

	public RepositoryProvider(AggregateConfigurer<T> aggregateClass) {
		this.aggregateClass = aggregateClass;
	}

	@Override
	public Repository<T> get() {
		return aggregateClass.repository();
	}

}
