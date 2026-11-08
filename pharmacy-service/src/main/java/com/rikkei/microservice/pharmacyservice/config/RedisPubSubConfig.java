package com.rikkei.microservice.pharmacyservice.config;

import com.rikkei.microservice.pharmacyservice.redis.DashboardAlertSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisPubSubConfig {

    public static final String CHANNEL_NAME = "pharmacy-alerts";

    /**
     * Đăng ký Adapter trỏ tới phương thức 'handleAlert' của DashboardAlertSubscriber
     */
    @Bean
    public MessageListenerAdapter alertListenerAdapter(DashboardAlertSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "handleAlert");
    }

    /**
     * Cấu hình Container lắng nghe channel 'pharmacy-alerts'
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter alertListenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(alertListenerAdapter, new ChannelTopic(CHANNEL_NAME));
        return container;
    }
}
