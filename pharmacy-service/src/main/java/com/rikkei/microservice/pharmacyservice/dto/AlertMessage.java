package com.rikkei.microservice.pharmacyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertMessage {
    private String topicListen; // Mặc định: "pharmacy-alerts"
    private String type;        // Ví dụ: "IMPORT"
    private String message;     // Ví dụ: "Đã nhập 100 hộp Panadol"
}
