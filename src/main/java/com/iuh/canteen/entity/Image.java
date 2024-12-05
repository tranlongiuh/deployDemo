package com.iuh.canteen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Image Hình ảnh
 */
@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    /**
     * Mã
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * dữ liệu byte thô lưu trữ hình ảnh
     */
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] data;

    /**
     * Loại nội dung (image/jpeg/...)
     */
    private String contentType;
}
