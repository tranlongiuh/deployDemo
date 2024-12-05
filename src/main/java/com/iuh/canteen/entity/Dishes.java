package com.iuh.canteen.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dishes Món ăn
 */
@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dishes {

    /**
     * Mã
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tên
     */
    @Column(nullable = false)
    private String name;

    /**
     * Mô tả
     */
    private String description;

    /**
     * Số lượng
     */
    private Integer quantity;

    /**
     * Số lượt đánh giá
     */
    private Integer reviews;

    /**
     * Mức đánh giá. Tối đa 5/5 tối thiểu 0/5
     */
    private Integer stars;

    /**
     * Giá
     */
    @Column(nullable = false)
    private BigDecimal price;

    /**
     * Mã Hình ảnh
     */
    private Long imageId;

    /**
     * Danh mục
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Mục đơn hàng
     */
    @OneToMany(mappedBy = "dishes", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<OrderItem> orderItems;

    /**
     * Gian hàng
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stall_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Stall stall;
}