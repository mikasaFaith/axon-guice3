/**
 * STARPOST CONFIDENTIAL
 * _____________________
 * 
 * [2014] - [2017] StarPost Supply Chain Management Co. (Shenzhen) Ltd. 
 * All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of
 * StarPost Supply Chain Management Co. (Shenzhen) Ltd. and its suppliers, if
 * any. The intellectual and technical concepts contained herein are proprietary
 * to StarPost Supply Chain Management Co. (Shenzhen) Ltd. and its suppliers and
 * may be covered by China and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from StarPost Supply Chain Management Co. (Shenzhen)
 * Ltd.
 *
 */
package org.axonframework.config.guice;

import org.axonframework.commandhandling.model.Repository;
import org.axonframework.config.guice.domain.TestAggregate;
import org.axonframework.messaging.GenericMessage;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;

import com.google.inject.Inject;

/**
 * @author kmtong
 *
 */
public class TestClassInjection {

	@Inject
	Repository<TestAggregate> repo;

	public Repository<TestAggregate> getRepo() {
		return repo;
	}

	public TestAggregate load(String id) throws Exception {
		return DefaultUnitOfWork.startAndGet(new GenericMessage<>(id))
				.executeWithResult(() -> repo.load(id).invoke(a -> a));
	}
}
