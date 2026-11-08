package com.rikkei.microservice.pharmacyservice.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikkei.microservice.pharmacyservice.dto.AlertMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardAlertSubscriber {

    private final ObjectMapper objectMapper;

    /**
     * Phương thức xử lý khi nhận được message từ channel 'pharmacy-alerts'.
     * In ra Console đúng y hệt định dạng trong ảnh mẫu của đề bài.
     */
    public void handleAlert(String jsonMessage) {
        try {
            AlertMessage alert = objectMapper.readValue(jsonMessage, AlertMessage.class);

            System.out.println("=== THÔNG BÁO DASHBOARD QUẢN LÝ ===");
            System.out.println("Type : " + (alert.getType() != null ? alert.getType() : "IMPORT"));
            System.out.println("Nội dung: " + (alert.getMessage() != null ? alert.getMessage() : jsonMessage));
            System.out.println("===================================");
        } catch (Exception e) {
            // Trường hợp message gửi dạng chuỗi đơn giản
            System.out.println("=== THÔNG BÁO DASHBOARD QUẢN LÝ ===");
            System.out.println("Type : IMPORT");
            System.out.println("Nội dung: " + jsonMessage);
            System.out.println("===================================");
        }
    }
}
