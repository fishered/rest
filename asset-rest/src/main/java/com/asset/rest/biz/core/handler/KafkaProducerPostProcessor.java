package com.asset.rest.biz.core.handler;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.asset.rest.biz.context.handler.HandlerContext;
import com.asset.rest.biz.context.handler.LinkedHandlerContext;
import com.asset.rest.biz.core.HandlerPostProcessor;
import com.asset.rest.enums.Handler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author fisher
 * @date 2023-10-10: 16:27
 */
@Component
@Slf4j
public class KafkaProducerPostProcessor<E extends LinkedHandlerContext> implements HandlerPostProcessor {
    @Override
    public void handler(HandlerContext handlerContext) {
        log.info("[kafka] handlerContext: {}", handlerContext);
        //如果handlerContext为 kafka handler 那么operaType为指定topic，data为额外添加的url param为kafka message
        isSupportThrowEx(handlerContext);
        String topic = handlerContext.getOperaType();
        String url = handlerContext.getHandlerData();
        String message = JSON.toJSONString(handlerContext.getParams());

        KafkaTemplate<String, String> customKafkaTemplate = createKafkaTemplate(url);
        customKafkaTemplate.setDefaultTopic(topic);

        // 检查并创建主题
        createTopicIfNotExist(customKafkaTemplate, topic);

        customKafkaTemplate.sendDefault(message);
    }

    private void isSupportThrowEx(HandlerContext handlerContext){
        Assert.notBlank(handlerContext.getHandlerData(), "kafka producer config url not null!");
        Assert.notBlank(handlerContext.getOperaType(), "kafka producer config topic not null!");
        Assert.notNull(handlerContext.getParams(), "kafka producer config message not null!");
    }

    @Override
    public Handler getHandler() {
        return Handler.KAFKA;
    }

    private KafkaTemplate<String, String> createKafkaTemplate(String bootstrapServers) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 可以根据需要设置其他的配置属性

        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        return new KafkaTemplate<>(producerFactory);
    }

    private void createTopicIfNotExist(KafkaTemplate<String, String> kafkaTemplate, String topic) {
        AdminClient adminClient = AdminClient.create(kafkaTemplate.getProducerFactory().getConfigurationProperties());
        ListTopicsResult topicsResult = adminClient.listTopics();

        try {
            Collection<TopicListing> topics = topicsResult.listings().get();
            for (TopicListing topicListing : topics) {
                if (topicListing.name().equals(topic)) {
                    // 主题已存在，无需创建
                    return;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            // 处理异常
            e.printStackTrace();
        }

        // 创建主题
        NewTopic newTopic = new NewTopic(topic, 1, (short) 1);
        CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singleton(newTopic));

        try {
            createTopicsResult.all().get();
        } catch (InterruptedException | ExecutionException e) {
            // 处理异常
            e.printStackTrace();
        }

        // 关闭adminClient
        adminClient.close();
    }
}
