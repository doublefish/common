package com.tt.common.amqp;

import com.rabbitmq.client.Channel;
import com.tt.common.Constants;
import com.tt.common.LoggerHelper;
import com.tt.common.RequestDataHelper;
import com.tt.common.RetryableException;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;

import java.io.IOException;
import java.util.UUID;

/**
 * BaseListener
 * 发生异常时，默认丢弃消息
 *
 * @author Shuang Yu
 */
@Slf4j
public abstract class BaseListener {

    /**
     * onMessage
     *
     * @param channel channel
     * @param message message
     */
    @RabbitHandler(isDefault = true)
    protected void onMessage(Message message, Channel channel) {
        var properties = message.getMessageProperties();
        var exchange = properties.getReceivedExchange();
        var routingKey = properties.getReceivedRoutingKey();
        var deliveryTag = properties.getDeliveryTag();
        var mills = System.currentTimeMillis();
        var result = ResultType.READY;
        try {
            MDC.put(Constants.MESSAGE_ID, UUID.randomUUID().toString());
            beforeExecute();
            var body = new String(message.getBody());
            log.info("接收消息=>exchange: {}, routingKey: {}，deliveryTag: {}，body: {}", exchange, routingKey, deliveryTag, body);
            if (StringUtils.isNotEmpty(body)) {
                result = execute(body, message.getMessageProperties());
            } else {
                result = ResultType.FAIL;
            }
        } catch (Exception e) {
            error(e);
            result = e instanceof RetryableException ? ResultType.RETRY : ResultType.EXCEPTION;
        } finally {
            log.info("处理结果: {}，耗时：{}ms", result, System.currentTimeMillis() - mills);
            ack(channel, deliveryTag, result);
            afterExecuted();
        }
    }

    /**
     * 执行
     *
     * @param body       body
     * @param properties properties
     * @return ResultType
     */
    protected abstract ResultType execute(String body, MessageProperties properties);

    /**
     * 答复
     *
     * @param channel     channel
     * @param deliveryTag deliveryTag
     * @param result      result
     */
    protected void ack(Channel channel, long deliveryTag, ResultType result) {
        try {
            if (result == ResultType.SUCCESS) {
                channel.basicAck(deliveryTag, false);
            } else {
                channel.basicNack(deliveryTag, false, result == ResultType.RETRY);
            }
        } catch (IOException e) {
            log.error("答复时发生异常：" + e, e);
        }
    }

    protected void beforeExecute() {
        // 子类根据需要重写此方法
    }

    protected void afterExecuted() {
        // 子类根据需要重写此方法
        RequestDataHelper.remove();
    }

    /**
     * 在beforeExecute和afterExecuted之间有效，非MQ的消息ID
     */
    protected String getMessageId() {
        return MDC.get(Constants.MESSAGE_ID);
    }

    protected void error(Exception e) {
        LoggerHelper.error(log, "处理消息发生异常：" + e, e);
    }
}
