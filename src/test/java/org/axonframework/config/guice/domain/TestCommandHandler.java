package org.axonframework.config.guice.domain;

import java.util.Date;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Repository;

import com.google.inject.Inject;

public class TestCommandHandler {

	@Inject
	Repository<TestAggregate> repo;

	@CommandHandler
	public Date currentDate(GetCurrentDateCommand cmd) {
		System.out.println("Injection should work: " + repo);
		return new Date();
	}

	public Repository<TestAggregate> getRepo() {
		return repo;
	}

}
