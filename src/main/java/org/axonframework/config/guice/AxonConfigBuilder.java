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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thoughtworks.xstream.converters.Converter;

/**
 * @author kmtong
 *
 */
public class AxonConfigBuilder {

	Set<Class<?>> aggregateClasses = Sets.newHashSet();
	Set<Class<?>> commandHandlerClasses = Sets.newHashSet();
	Set<Class<?>> eventHandlerClasses = Sets.newHashSet();
	Map<String, Set<Class<?>>> trackingEventHandlerClasses = Maps.newHashMap();
	Set<Class<?>> sagaClasses = Sets.newHashSet();
	Set<Class<?>> trackingSagaClasses = Sets.newHashSet();
	Set<Class<?>> commandGatewayClasses = Sets.newHashSet();
	Set<Class<? extends Converter>> converterClasses = Sets.newHashSet();

	public AxonConfigBuilder withAggregate(Class<?> aggregateClass) {
		this.aggregateClasses.add(aggregateClass);
		return this;
	}

	public AxonConfigBuilder withAggregates(Set<Class<?>> aggregateClasses) {
		this.aggregateClasses.addAll(aggregateClasses);
		return this;
	}

	public AxonConfigBuilder withSaga(Class<?> sagaClass) {
		this.sagaClasses.add(sagaClass);
		return this;
	}

	public AxonConfigBuilder withSagas(Set<Class<?>> sagaClass) {
		this.sagaClasses.addAll(sagaClass);
		return this;
	}

	public AxonConfigBuilder withTrackingSaga(Class<?> sagaClass) {
		this.trackingSagaClasses.add(sagaClass);
		return this;
	}

	public AxonConfigBuilder withTrackingSagas(Set<Class<?>> sagaClass) {
		this.trackingSagaClasses.addAll(sagaClass);
		return this;
	}

	public AxonConfigBuilder withCommandHandler(Class<?> commandHandler) {
		this.commandHandlerClasses.add(commandHandler);
		return this;
	}

	public AxonConfigBuilder withCommandHandlers(Set<Class<?>> commandHandlers) {
		this.commandHandlerClasses.addAll(commandHandlers);
		return this;
	}

	public AxonConfigBuilder withEventHandler(Class<?> eventHandler) {
		this.eventHandlerClasses.add(eventHandler);
		return this;
	}

	public AxonConfigBuilder withEventHandlers(Set<Class<?>> eventHandlers) {
		this.eventHandlerClasses.addAll(eventHandlers);
		return this;
	}

	public AxonConfigBuilder withTrackingEventHandler(String name, Class<?> trackingEventHandler) {
		ensureNameExists(this.trackingEventHandlerClasses, name);
		this.trackingEventHandlerClasses.get(name).add(trackingEventHandler);
		return this;
	}

	public AxonConfigBuilder withTrackingEventHandlers(String name, Set<Class<?>> trackingEventHandlers) {
		ensureNameExists(this.trackingEventHandlerClasses, name);
		this.trackingEventHandlerClasses.get(name).addAll(trackingEventHandlers);
		return this;
	}

	public AxonConfigBuilder withCommandGateway(Class<?> commandGateway) {
		this.commandGatewayClasses.add(commandGateway);
		return this;
	}

	public AxonConfigBuilder withCommandGateways(Set<Class<?>> commandGateways) {
		this.commandGatewayClasses.addAll(commandGateways);
		return this;
	}

	public AxonConfigBuilder withXStreamConverter(Class<? extends Converter> converter) {
		this.converterClasses.add(converter);
		return this;
	}

	public AxonConfig build() {
		return new AxonConfig(aggregateClasses, commandHandlerClasses, eventHandlerClasses, trackingEventHandlerClasses,
				sagaClasses, trackingSagaClasses, commandGatewayClasses, converterClasses);
	}

	public static AxonConfigBuilder create() {
		return new AxonConfigBuilder();
	}

	private static void ensureNameExists(Map<String, Set<Class<?>>> map, String name) {
		if (!map.containsKey(name) || map.get(name) == null) {
			map.put(name, Sets.newHashSet());
		}
	}

}
