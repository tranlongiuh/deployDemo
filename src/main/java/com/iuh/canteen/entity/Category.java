package com.iuh.canteen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Category Danh mục
 */
@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    /**
     * Mã Danh mục
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tên Danh mục
     */
    @Column(nullable = false, unique = true)
    private String name;
}