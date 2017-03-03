package org.axonframework.config.guice.domain;

import java.util.Date;

import org.axonframework.commandhandling.CommandHandler;

public class TestCommandHandler {

	@CommandHandler
	public Date currentDate(GetCurrentDateCommand cmd) {
		return new Date();
	}
}
