package com.rikkei.microservice.pharmacyservice.controller;

import com.rikkei.microservice.pharmacyservice.dto.AlertMessage;
import com.rikkei.microservice.pharmacyservice.redis.PharmacyAlertPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final PharmacyAlertPublisher alertPublisher;

    /**
     * API gửi thông báo Redis Pub/Sub:
     * POST http://localhost:8081/api/v1/alerts
     * Body:
     * {
     *     "topicListen" : "pharmacy-alerts",
     *     "type" : "IMPORT",
     *     "message" : "Đã nhập 100 hộp Panadol"
     * }
     */
    @PostMapping
    public ResponseEntity<String> sendAlert(@RequestBody AlertMessage request) {
        if (request.getTopicListen() == null || request.getTopicListen().isBlank()) {
            request.setTopicListen("pharmacy-alerts");
        }
        if (request.getType() == null || request.getType().isBlank()) {
            request.setType("IMPORT");
        }

        // Bắn thông báo qua Redis Pub/Sub
        alertPublisher.publishAlert(request);

        // Trả về đúng nội dung như trong ảnh Postman của đề bài
        return ResponseEntity.ok("Đã gửi thông báo đến kênh : " + request.getTopicListen());
    }
}
