package com.iuh.canteen.repository;

import com.iuh.canteen.common.PaymentStatus;
import com.iuh.canteen.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStatus(PaymentStatus status);
    // Thêm các phương thức tìm kiếm khác nếu cần
}