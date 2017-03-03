/**
 * STARPOST CONFIDENTIAL
 * _____________________
 * 
 * [2014] - [2016] StarPost Supply Chain Management Co. (Shenzhen) Ltd. 
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

import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.converters.Converter;

/**
 * @author kmtong
 *
 */
public class AxonConfig {

	final Set<Class<?>> aggregateClasses;
	final Set<Class<?>> commandHandlerClasses;
	final Set<Class<?>> eventHandlerClasses;
	final Map<String, Set<Class<?>>> trackingEventHandlerClasses;
	final Set<Class<?>> sagaClasses;
	final Set<Class<?>> trackingSagaClasses;
	final Set<Class<?>> commandGatewayClasses;
	final Set<Class<? extends Converter>> converterClasses;

	public AxonConfig(Set<Class<?>> aggregateClasses, Set<Class<?>> commandHandlerClasses,
			Set<Class<?>> eventHandlerClasses, Map<String, Set<Class<?>>> trackingEventHandlerClasses,
			Set<Class<?>> sagaClasses, Set<Class<?>> trackingSagaClasses, Set<Class<?>> commandGatewayClasses,
			Set<Class<? extends Converter>> converterClasses) {
		this.aggregateClasses = aggregateClasses;
		this.commandHandlerClasses = commandHandlerClasses;
		this.eventHandlerClasses = eventHandlerClasses;
		this.trackingEventHandlerClasses = trackingEventHandlerClasses;
		this.sagaClasses = sagaClasses;
		this.trackingSagaClasses = trackingSagaClasses;
		this.commandGatewayClasses = commandGatewayClasses;
		this.converterClasses = converterClasses;
	}

	public Set<Class<?>> getAggregateClasses() {
		return aggregateClasses;
	}

	public Set<Class<?>> getCommandHandlerClasses() {
		return commandHandlerClasses;
	}

	public Set<Class<?>> getEventHandlerClasses() {
		return eventHandlerClasses;
	}

	public Map<String, Set<Class<?>>> getTrackingEventHandlerClasses() {
		return trackingEventHandlerClasses;
	}

	public Set<Class<?>> getSagaClasses() {
		return sagaClasses;
	}

	public Set<Class<?>> getTrackingSagaClasses() {
		return trackingSagaClasses;
	}

	public Set<Class<?>> getCommandGatewayClasses() {
		return commandGatewayClasses;
	}

	public Set<Class<? extends Converter>> getEventSerializerConverterClasses() {
		return converterClasses;
	}

	public Set<Class<? extends Converter>> getConverterClasses() {
		return converterClasses;
	}

}
