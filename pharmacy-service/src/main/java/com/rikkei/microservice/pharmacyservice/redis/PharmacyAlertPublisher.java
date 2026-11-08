package com.rikkei.microservice.pharmacyservice.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikkei.microservice.pharmacyservice.config.RedisPubSubConfig;
import com.rikkei.microservice.pharmacyservice.dto.AlertMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PharmacyAlertPublisher {

    private static final Logger log = LoggerFactory.getLogger(PharmacyAlertPublisher.class);
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Gửi message vào Redis Pub/Sub channel:
     * Sử dụng ObjectMapper để serialize cả đối tượng AlertMessage thành chuỗi JSON.
     */
    public void publishAlert(AlertMessage alertMessage) {
        String channel = (alertMessage.getTopicListen() != null && !alertMessage.getTopicListen().isBlank())
                ? alertMessage.getTopicListen()
                : RedisPubSubConfig.CHANNEL_NAME;

        try {
            // Chuyển đối tượng sang JSON String bằng ObjectMapper
            String jsonMessage = objectMapper.writeValueAsString(alertMessage);
            log.info(">> [REDIS PUBLISHER] Đang phát thông báo tới channel '{}': {}", channel, jsonMessage);

            // Bắn message qua Redis Pub/Sub (Fire-and-forget)
            stringRedisTemplate.convertAndSend(channel, jsonMessage);
        } catch (Exception e) {
            log.error(">> Lỗi khi serialize JSON hoặc gửi Redis Pub/Sub: {}", e.getMessage());
        }
    }
}
