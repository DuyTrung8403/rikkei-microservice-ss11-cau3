package com.rikkei.microservice.pharmacyservice.controller;

import com.rikkei.microservice.pharmacyservice.entity.Medicine;
import com.rikkei.microservice.pharmacyservice.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    /**
     * API 1: Lấy chi tiết thuốc theo ID để test Redis Cache:
     * GET http://localhost:8081/api/medicines/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMedicine(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();

        Medicine medicine = medicineService.getMedicineById(id);

        long responseTime = System.currentTimeMillis() - startTime;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("medicine", medicine);
        response.put("responseTimeMs", responseTime + " ms");
        response.put("dataSource", responseTime < 20 ? "REDIS_CACHE (Tốc độ cực nhanh < 10ms)" : "POSTGRESQL_DATABASE (Lần đầu nạp cache)");

        return ResponseEntity.ok(response);
    }

    /**
     * API 2: Cập nhật thông tin/giá thuốc (Kích hoạt @CacheEvict để xóa cache cũ khỏi Redis):
     * PUT http://localhost:8081/api/medicines/1
     * Body: { "price": 35000.0 }
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMedicine(
            @PathVariable Long id,
            @RequestBody Medicine request) {
        request.setId(id);
        Medicine updated = medicineService.updateMedicine(request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Cập nhật giá thuốc thành công! Key 'medicines::" + id + "' đã bị XÓA khỏi Redis.");
        response.put("updatedMedicine", updated);

        return ResponseEntity.ok(response);
    }

    /**
     * API 3: Cập nhật giá nhanh qua Query Param (rất tiện test bằng Postman):
     * PUT http://localhost:8081/api/medicines/1/price?newPrice=35000
     */
    @PutMapping("/{id}/price")
    public ResponseEntity<Map<String, Object>> updatePrice(
            @PathVariable Long id,
            @RequestParam Double newPrice) {
        Medicine medicine = Medicine.builder()
                .id(id)
                .price(newPrice)
                .build();
        Medicine updated = medicineService.updateMedicine(medicine);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Cập nhật giá thuốc thành công (" + newPrice + " VND)! Key 'medicines::" + id + "' đã bị XÓA khỏi Redis.");
        response.put("updatedMedicine", updated);

        return ResponseEntity.ok(response);
    }
}
