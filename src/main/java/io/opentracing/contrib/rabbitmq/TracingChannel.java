/*
 * Copyright 2017-2018 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.opentracing.contrib.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Command;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ConsumerShutdownSignalCallback;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.Method;
import com.rabbitmq.client.ReturnCallback;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;


public class TracingChannel implements Channel {

  private final Channel channel;
  private final Tracer tracer;

  public TracingChannel(Channel channel, Tracer tracer) {
    this.channel = channel;
    this.tracer = tracer;
  }

  @Override
  public int getChannelNumber() {
    return channel.getChannelNumber();
  }

  @Override
  public Connection getConnection() {
    return channel.getConnection();
  }

  @Override
  public void close() throws IOException, TimeoutException {
    channel.close();
  }

  @Override
  public void close(int closeCode, String closeMessage) throws IOException, TimeoutException {
    channel.close(closeCode, closeMessage);
  }

  @Override
  public void abort() throws IOException {
    channel.abort();
  }

  @Override
  public void abort(int closeCode, String closeMessage) throws IOException {
    channel.abort(closeCode, closeMessage);
  }

  @Override
  public void addReturnListener(ReturnListener listener) {
    channel.addReturnListener(listener);
  }

  @Override
  public ReturnListener addReturnListener(ReturnCallback returnCallback) {
    return channel.addReturnListener(returnCallback);
  }

  @Override
  public boolean removeReturnListener(ReturnListener listener) {
    return channel.removeReturnListener(listener);
  }

  @Override
  public void clearReturnListeners() {
    channel.clearReturnListeners();
  }

  @Override
  public void addConfirmListener(ConfirmListener listener) {
    channel.addConfirmListener(listener);
  }

  @Override
  public ConfirmListener addConfirmListener(ConfirmCallback confirmCallback,
      ConfirmCallback confirmCallback1) {
    return channel.addConfirmListener(confirmCallback, confirmCallback1);
  }

  @Override
  public boolean removeConfirmListener(ConfirmListener listener) {
    return channel.removeConfirmListener(listener);
  }

  @Override
  public void clearConfirmListeners() {
    channel.clearConfirmListeners();
  }

  @Override
  public Consumer getDefaultConsumer() {
    return channel.getDefaultConsumer();
  }

  @Override
  public void setDefaultConsumer(Consumer consumer) {
    channel.setDefaultConsumer(consumer);
  }

  @Override
  public void basicQos(int prefetchSize, int prefetchCount, boolean global) throws IOException {
    channel.basicQos(prefetchSize, prefetchCount, global);
  }

  @Override
  public void basicQos(int prefetchCount, boolean global) throws IOException {
    channel.basicQos(prefetchCount, global);
  }

  @Override
  public void basicQos(int prefetchCount) throws IOException {
    channel.basicQos(prefetchCount);
  }

  @Override
  public void basicPublish(String exchange, String routingKey, AMQP.BasicProperties props,
      byte[] body) throws IOException {
    basicPublish(exchange, routingKey, false, false, props, body);
  }

  @Override
  public void basicPublish(String exchange, String routingKey, boolean mandatory,
      AMQP.BasicProperties props, byte[] body) throws IOException {
    basicPublish(exchange, routingKey, mandatory, false, props, body);
  }

  @Override
  public void basicPublish(String exchange, String routingKey, boolean mandatory, boolean immediate,
      AMQP.BasicProperties props, byte[] body) throws IOException {

    try (Scope scope = buildSpan(exchange, props)) {
      AMQP.BasicProperties properties = inject(props, scope.span());
      channel.basicPublish(exchange, routingKey, mandatory, immediate, properties, body);
    }
  }

  @Override
  public AMQP.Exchange.DeclareOk exchangeDeclare(String exchange, String type) throws IOException {
    return channel.exchangeDeclare(exchange, type);
  }

  @Override
  public AMQP.Exchange.DeclareOk exchangeDeclare(String exchange, BuiltinExchangeType type)
      throws IOException {
    return channel.exchangeDeclare(exchange, type);
  }

  @Override
  public AMQP.Exchange.DeclareOk exchangeDeclare(String exchange, String type, boolean durable)
      throws IOException {
    return channel.exchangeDeclare(exchange, type, durable);
  }

  @Override
  public AMQP.Exchange.DeclareOk exchangeDeclare(String exchange, BuiltinExchangeType type,
      boolean durable) throws IOException {
    return channel.exchangeDeclare(exchange, type, durable);
  }

  @Override
  public AMQP.Exchange.DeclareOk exchangeDeclare(String exchange, String type, boolean durable,
      boolean autoDelete, Map<String, Object> arguments) throws IOException {
    return channel.exchangeDeclare(exchange, type, durable, autoDelete, arguments);
  }

  @Override
  public AMQP.Exchange.DeclareOk exchangeDeclare(String exchange, BuiltinExchangeType type,
      boolean durable, boolean autoDelete, Map<String, Object> arguments) throws IOException {
    return channel.exchangeDeclare(exchange, type, durable, autoDelete, arguments);
  }

  @Override
  public AMQP.Exchange.DeclareOk exchangeDeclare(String exchange, String type, boolean durable,
      boolean autoDelete, boolean internal, Map<String, Object> arguments) throws IOException {
    return channel.exchangeDeclare(exchange, type, durable, autoDelete, internal, arguments);
  }

  @Override
  public AMQP.Exchange.DeclareOk exchangeDeclare(String exchange, BuiltinExchangeType type,
      boolean durable, boolean autoDelete, boolean internal, Map<String, Object> arguments)
      throws IOException {
    return channel.exchangeDeclare(exchange, type, durable, autoDelete, internal, arguments);
  }

  @Override
  public void exchangeDeclareNoWait(String exchange, String type, boolean durable,
      boolean autoDelete, boolean internal, Map<String, Object> arguments) throws IOException {
    channel.exchangeDeclareNoWait(exchange, type, durable, autoDelete, internal, arguments);
  }

  @Override
  public void exchangeDeclareNoWait(String exchange, BuiltinExchangeType type, boolean durable,
      boolean autoDelete, boolean internal, Map<String, Object> arguments) throws IOException {
    channel.exchangeDeclareNoWait(exchange, type, durable, autoDelete, internal, arguments);
  }

  @Override
  public AMQP.Exchange.DeclareOk exchangeDeclarePassive(String name) throws IOException {
    return channel.exchangeDeclarePassive(name);
  }

  @Override
  public AMQP.Exchange.DeleteOk exchangeDelete(String exchange, boolean ifUnused)
      throws IOException {
    return channel.exchangeDelete(exchange, ifUnused);
  }

  @Override
  public void exchangeDeleteNoWait(String exchange, boolean ifUnused) throws IOException {
    channel.exchangeDeleteNoWait(exchange, ifUnused);
  }

  @Override
  public AMQP.Exchange.DeleteOk exchangeDelete(String exchange) throws IOException {
    return channel.exchangeDelete(exchange);
  }

  @Override
  public AMQP.Exchange.BindOk exchangeBind(String destination, String source, String routingKey)
      throws IOException {
    return channel.exchangeBind(destination, source, routingKey);
  }

  @Override
  public AMQP.Exchange.BindOk exchangeBind(String destination, String source, String routingKey,
      Map<String, Object> arguments) throws IOException {
    return channel.exchangeBind(destination, source, routingKey, arguments);
  }

  @Override
  public void exchangeBindNoWait(String destination, String source, String routingKey,
      Map<String, Object> arguments) throws IOException {
    channel.exchangeBindNoWait(destination, source, routingKey, arguments);
  }

  @Override
  public AMQP.Exchange.UnbindOk exchangeUnbind(String destination, String source, String routingKey)
      throws IOException {
    return channel.exchangeUnbind(destination, source, routingKey);
  }

  @Override
  public AMQP.Exchange.UnbindOk exchangeUnbind(String destination, String source, String routingKey,
      Map<String, Object> arguments) throws IOException {
    return channel.exchangeUnbind(destination, source, routingKey, arguments);
  }

  @Override
  public void exchangeUnbindNoWait(String destination, String source, String routingKey,
      Map<String, Object> arguments) throws IOException {
    channel.exchangeUnbindNoWait(destination, source, routingKey, arguments);
  }

  @Override
  public AMQP.Queue.DeclareOk queueDeclare() throws IOException {
    return channel.queueDeclare();
  }

  @Override
  public AMQP.Queue.DeclareOk queueDeclare(String queue, boolean durable, boolean exclusive,
      boolean autoDelete, Map<String, Object> arguments) throws IOException {
    return channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments);
  }

  @Override
  public void queueDeclareNoWait(String queue, boolean durable, boolean exclusive,
      boolean autoDelete, Map<String, Object> arguments) throws IOException {
    channel.queueDeclareNoWait(queue, durable, exclusive, autoDelete, arguments);
  }

  @Override
  public AMQP.Queue.DeclareOk queueDeclarePassive(String queue) throws IOException {
    return channel.queueDeclarePassive(queue);
  }

  @Override
  public AMQP.Queue.DeleteOk queueDelete(String queue) throws IOException {
    return channel.queueDelete(queue);
  }

  @Override
  public AMQP.Queue.DeleteOk queueDelete(String queue, boolean ifUnused, boolean ifEmpty)
      throws IOException {
    return channel.queueDelete(queue, ifUnused, ifEmpty);
  }

  @Override
  public void queueDeleteNoWait(String queue, boolean ifUnused, boolean ifEmpty)
      throws IOException {
    channel.queueDeleteNoWait(queue, ifUnused, ifEmpty);
  }

  @Override
  public AMQP.Queue.BindOk queueBind(String queue, String exchange, String routingKey)
      throws IOException {
    return channel.queueBind(queue, exchange, routingKey);
  }

  @Override
  public AMQP.Queue.BindOk queueBind(String queue, String exchange, String routingKey,
      Map<String, Object> arguments) throws IOException {
    return channel.queueBind(queue, exchange, routingKey, arguments);
  }

  @Override
  public void queueBindNoWait(String queue, String exchange, String routingKey,
      Map<String, Object> arguments) throws IOException {
    channel.queueBindNoWait(queue, exchange, routingKey, arguments);
  }

  @Override
  public AMQP.Queue.UnbindOk queueUnbind(String queue, String exchange, String routingKey)
      throws IOException {
    return channel.queueUnbind(queue, exchange, routingKey);
  }

  @Override
  public AMQP.Queue.UnbindOk queueUnbind(String queue, String exchange, String routingKey,
      Map<String, Object> arguments) throws IOException {
    return channel.queueUnbind(queue, exchange, routingKey, arguments);
  }

  @Override
  public AMQP.Queue.PurgeOk queuePurge(String queue) throws IOException {
    return channel.queuePurge(queue);
  }

  @Override
  public GetResponse basicGet(String queue, boolean autoAck) throws IOException {
    GetResponse response = channel.basicGet(queue, autoAck);
    TracingUtils.buildAndFinishChildSpan(response.getProps(), tracer);
    return response;
  }

  @Override
  public void basicAck(long deliveryTag, boolean multiple) throws IOException {
    channel.basicAck(deliveryTag, multiple);
  }

  @Override
  public void basicNack(long deliveryTag, boolean multiple, boolean requeue) throws IOException {
    channel.basicNack(deliveryTag, multiple, requeue);
  }

  @Override
  public void basicReject(long deliveryTag, boolean requeue) throws IOException {
    channel.basicReject(deliveryTag, requeue);
  }

  @Override
  public String basicConsume(String queue, Consumer callback) throws IOException {
    return basicConsume(queue, false, "", false, false, null, callback);
  }

  @Override
  public String basicConsume(String s, DeliverCallback deliverCallback,
      CancelCallback cancelCallback) throws IOException {
    return channel.basicConsume(s, deliverCallback, cancelCallback);
  }

  @Override
  public String basicConsume(String s, DeliverCallback deliverCallback,
      ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
    return channel.basicConsume(s, deliverCallback, consumerShutdownSignalCallback);
  }

  @Override
  public String basicConsume(String s, DeliverCallback deliverCallback,
      CancelCallback cancelCallback,
      ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
    return channel.basicConsume(s, deliverCallback, cancelCallback, consumerShutdownSignalCallback);
  }

  @Override
  public String basicConsume(String queue, boolean autoAck, Consumer callback) throws IOException {
    return basicConsume(queue, autoAck, "", false, false, null, callback);
  }

  @Override
  public String basicConsume(String s, boolean b, DeliverCallback deliverCallback,
      CancelCallback cancelCallback) throws IOException {
    return channel.basicConsume(s, b, deliverCallback, cancelCallback);
  }

  @Override
  public String basicConsume(String s, boolean b, DeliverCallback deliverCallback,
      ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
    return channel.basicConsume(s, b, deliverCallback, consumerShutdownSignalCallback);
  }

  @Override
  public String basicConsume(String s, boolean b, DeliverCallback deliverCallback,
      CancelCallback cancelCallback,
      ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
    return channel
        .basicConsume(s, b, deliverCallback, cancelCallback, consumerShutdownSignalCallback);
  }

  @Override
  public String basicConsume(String queue, boolean autoAck, Map<String, Object> arguments,
      Consumer callback) throws IOException {
    return basicConsume(queue, autoAck, "", false, false, arguments, callback);
  }

  @Override
  public String basicConsume(String s, boolean b,
      Map<String, Object> map, DeliverCallback deliverCallback,
      CancelCallback cancelCallback) throws IOException {
    return channel.basicConsume(s, b, map, deliverCallback, cancelCallback);
  }

  @Override
  public String basicConsume(String s, boolean b,
      Map<String, Object> map, DeliverCallback deliverCallback,
      ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
    return channel.basicConsume(s, b, map, deliverCallback, consumerShutdownSignalCallback);
  }

  @Override
  public String basicConsume(String s, boolean b,
      Map<String, Object> map, DeliverCallback deliverCallback,
      CancelCallback cancelCallback,
      ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
    return channel
        .basicConsume(s, b, map, deliverCallback, cancelCallback, consumerShutdownSignalCallback);
  }

  @Override
  public String basicConsume(String queue, boolean autoAck, String consumerTag, Consumer callback)
      throws IOException {
    return basicConsume(queue, autoAck, consumerTag, false, false, null, callback);
  }

  @Override
  public String basicConsume(String s, boolean b, String s1,
      DeliverCallback deliverCallback,
      CancelCallback cancelCallback) throws IOException {
    return channel.basicConsume(s, b, s1, deliverCallback, cancelCallback);
  }

  @Override
  public String basicConsume(String s, boolean b, String s1,
      DeliverCallback deliverCallback,
      ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
    return channel.basicConsume(s, b, s1, deliverCallback, consumerShutdownSignalCallback);
  }

  @Override
  public String basicConsume(String s, boolean b, String s1,
      DeliverCallback deliverCallback,
      CancelCallback cancelCallback,
      ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
    return channel
        .basicConsume(s, b, s1, deliverCallback, cancelCallback, consumerShutdownSignalCallback);
  }

  @Override
  public String basicConsume(String queue, boolean autoAck, String consumerTag, boolean noLocal,
      boolean exclusive, Map<String, Object> arguments, Consumer callback) throws IOException {
    return channel.basicConsume(queue, autoAck, consumerTag, noLocal, exclusive, arguments,
        new TracingConsumer(callback, tracer));
  }

  @Override
  public String basicConsume(String s, boolean b, String s1, boolean b1, boolean b2,
      Map<String, Object> map, DeliverCallback deliverCallback,
      CancelCallback cancelCallback) throws IOException {
    return channel.basicConsume(s, b, s1, b1, b2, map, deliverCallback, cancelCallback);
  }

  @Override
  public String basicConsume(String s, boolean b, String s1, boolean b1, boolean b2,
      Map<String, Object> map, DeliverCallback deliverCallback,
      ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
    return channel
        .basicConsume(s, b, s1, b1, b2, map, deliverCallback, consumerShutdownSignalCallback);
  }

  @Override
  public String basicConsume(String s, boolean b, String s1, boolean b1, boolean b2,
      Map<String, Object> map, DeliverCallback deliverCallback,
      CancelCallback cancelCallback,
      ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
    return channel.basicConsume(s, b, s1, b1, b2, map, deliverCallback, cancelCallback,
        consumerShutdownSignalCallback);
  }

  @Override
  public void basicCancel(String consumerTag) throws IOException {
    channel.basicCancel(consumerTag);
  }

  @Override
  public AMQP.Basic.RecoverOk basicRecover() throws IOException {
    return channel.basicRecover();
  }

  @Override
  public AMQP.Basic.RecoverOk basicRecover(boolean requeue) throws IOException {
    return channel.basicRecover(requeue);
  }

  @Override
  public AMQP.Tx.SelectOk txSelect() throws IOException {
    return channel.txSelect();
  }

  @Override
  public AMQP.Tx.CommitOk txCommit() throws IOException {
    return channel.txCommit();
  }

  @Override
  public AMQP.Tx.RollbackOk txRollback() throws IOException {
    return channel.txRollback();
  }

  @Override
  public AMQP.Confirm.SelectOk confirmSelect() throws IOException {
    return channel.confirmSelect();
  }

  @Override
  public long getNextPublishSeqNo() {
    return channel.getNextPublishSeqNo();
  }

  @Override
  public boolean waitForConfirms() throws InterruptedException {
    return channel.waitForConfirms();
  }

  @Override
  public boolean waitForConfirms(long timeout) throws InterruptedException, TimeoutException {
    return channel.waitForConfirms(timeout);
  }

  @Override
  public void waitForConfirmsOrDie() throws IOException, InterruptedException {
    channel.waitForConfirmsOrDie();
  }

  @Override
  public void waitForConfirmsOrDie(long timeout)
      throws IOException, InterruptedException, TimeoutException {
    channel.waitForConfirmsOrDie(timeout);
  }

  @Override
  public void asyncRpc(Method method) throws IOException {
    channel.asyncRpc(method);
  }

  @Override
  public Command rpc(Method method) throws IOException {
    return channel.rpc(method);
  }

  @Override
  public long messageCount(String queue) throws IOException {
    return channel.messageCount(queue);
  }

  @Override
  public long consumerCount(String queue) throws IOException {
    return channel.consumerCount(queue);
  }

  @Override
  public CompletableFuture<Command> asyncCompletableRpc(
      Method method) throws IOException {
    return channel.asyncCompletableRpc(method);
  }

  @Override
  public void addShutdownListener(ShutdownListener listener) {
    channel.addShutdownListener(listener);
  }

  @Override
  public void removeShutdownListener(ShutdownListener listener) {
    channel.removeShutdownListener(listener);
  }

  @Override
  public ShutdownSignalException getCloseReason() {
    return channel.getCloseReason();
  }

  @Override
  public void notifyListeners() {
    channel.notifyListeners();
  }

  @Override
  public boolean isOpen() {
    return channel.isOpen();
  }

  private Scope buildSpan(String exchange, AMQP.BasicProperties props) {
    Tracer.SpanBuilder spanBuilder = tracer.buildSpan("send")
        .ignoreActiveSpan()
        .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_PRODUCER);

    SpanContext spanContext = null;

    if (props != null && props.getHeaders() != null) {
      // just in case if span context was injected manually to props in basicPublish
      spanContext = tracer.extract(Format.Builtin.TEXT_MAP,
          new HeadersMapExtractAdapter(props.getHeaders()));
    }

    if (spanContext == null) {
      Span parentSpan = tracer.activeSpan();
      if (parentSpan != null) {
        spanContext = parentSpan.context();
      }
    }

    if (spanContext != null) {
      spanBuilder.asChildOf(spanContext);
    }

    Scope scope = spanBuilder.startActive(true);
    SpanDecorator.onRequest(exchange, scope.span());

    return scope;
  }

  private AMQP.BasicProperties inject(AMQP.BasicProperties properties, Span span) {

    // Headers of AMQP.BasicProperties is unmodifiableMap therefore we build new AMQP.BasicProperties
    // with injected span context into headers
    Map<String, Object> headers = new HashMap<>();

    tracer.inject(span.context(), Format.Builtin.TEXT_MAP, new HeadersMapInjectAdapter(headers));

    if (properties == null) {
      return new AMQP.BasicProperties().builder().headers(headers).build();
    }

    if (properties.getHeaders() != null) {
      headers.putAll(properties.getHeaders());
    }

    return properties.builder()
        .headers(headers)
        .build();
  }
}
