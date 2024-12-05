package com.iuh.canteen.common;

/**
 * OrderStatus Trạng thái cho đơn đặt hàng và món ăn trong đơn đặt hàng
 * NEW: Vừa tạo ra và chưa thanh toán
 * PROCESSING: Đang chuẩn bị món
 * COMPLETED: Đã hoàn thành đơn hàng
 * PENDING: Chưa giải quyết
 * PAID: Khách hàng đã thanh toán thành công
 * CANCELLED: Đơn đã bị hủy
 * WAITING: Đang chờ thực khách đến nhận món
 * SHIPPING: Đang trên đường giao đến thực khách
 */
public enum OrderStatus {
    NEW, PROCESSING, COMPLETED, PENDING, PAID, CANCELLED, WAITING, SHIPPING
}
