package org.axonframework.config.guice.provider;

import org.axonframework.commandhandling.model.Repository;
import org.axonframework.config.AggregateConfiguration;

import com.google.inject.Provider;

public class RepositoryProvider<T> implements Provider<Repository<T>> {

	final AggregateConfiguration<T> aggregateClass;

	public RepositoryProvider(AggregateConfiguration<T> aggregateClass) {
		this.aggregateClass = aggregateClass;
	}

	@Override
	public Repository<T> get() {
		return aggregateClass.repository();
	}

}
