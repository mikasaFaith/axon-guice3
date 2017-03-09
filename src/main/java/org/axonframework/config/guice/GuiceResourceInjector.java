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

import org.axonframework.eventhandling.saga.ResourceInjector;

import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * @author kmtong
 *
 */
public class GuiceResourceInjector implements ResourceInjector {

	final Provider<Injector> injector;

	public GuiceResourceInjector(Provider<Injector> injector) {
		this.injector = injector;
	}

	@Override
	public void injectResources(Object saga) {
		injector.get().injectMembers(saga);
	}

}
