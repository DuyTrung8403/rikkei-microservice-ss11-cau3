package com.rikkei.microservice.pharmacyservice.service;

import com.rikkei.microservice.pharmacyservice.entity.Medicine;
import com.rikkei.microservice.pharmacyservice.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private static final Logger log = LoggerFactory.getLogger(MedicineService.class);
    private final MedicineRepository medicineRepository;

    /**
     * Lấy chi tiết thuốc theo ID.
     * Annotation @Cacheable(value = "medicines", key = "#id"):
     * - Lần 1: Chưa có trong Redis -> Chạy phương thức bên dưới (truy vấn Database qua Hibernate SQL)
     *   và tự động lưu kết quả vào Redis dưới key 'medicines::[id]'.
     * - Lần 2: Đã có trong Redis -> Trả về kết quả trực tiếp từ Redis (không chạy code bên dưới, không có SQL).
     */
    @Cacheable(value = "medicines", key = "#id")
    public Medicine getMedicineById(Long id) {
        log.info(">> [DATABASE QUERY] Gọi xuống Database truy vấn thông tin thuốc ID = {}...", id);

        return medicineRepository.findById(id).orElseGet(() -> {
            log.info(">> Khởi tạo thuốc mặc định ID = {} vào Database", id);
            Medicine newMedicine = Medicine.builder()
                    .medicineName("Panadol Extra 500mg")
                    .description("Thuốc giảm đau hạ sốt tác dụng nhanh")
                    .price(25000.0)
                    .stock(500)
                    .build();
            return medicineRepository.save(newMedicine);
        });
    }

    /**
     * Cập nhật thông tin / sửa giá thuốc.
     * Annotation @CacheEvict(value = "medicines", key = "#medicine.id"):
     * - Khi một loại thuốc được sửa giá, Cache cũ trong Redis lập tức BỊ XÓA NGAY LẬP TỨC.
     * - Nhờ đó, lần truy vấn sau hệ thống sẽ bắt buộc phải gọi lại DB để nạp lại giá mới nhất!
     */
    @CacheEvict(value = "medicines", key = "#medicine.id")
    public Medicine updateMedicine(Medicine medicine) {
        log.info(">> [DATABASE UPDATE] Đang cập nhật thuốc ID = {} với giá mới: {} VND vào Database...",
                medicine.getId(), medicine.getPrice());

        Medicine existing = medicineRepository.findById(medicine.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc với ID: " + medicine.getId()));

        if (medicine.getPrice() != null) {
            existing.setPrice(medicine.getPrice());
        }
        if (medicine.getMedicineName() != null) {
            existing.setMedicineName(medicine.getMedicineName());
        }
        if (medicine.getDescription() != null) {
            existing.setDescription(medicine.getDescription());
        }
        if (medicine.getStock() != null) {
            existing.setStock(medicine.getStock());
        }

        Medicine updated = medicineRepository.save(existing);
        log.info(">> [CACHE EVICTED] Đã xóa cache cũ của key 'medicines::{}' trong Redis!", medicine.getId());
        return updated;
    }
}
