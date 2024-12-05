package com.iuh.canteen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Schedule Lịch trình
 */
@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

    /**
     * Mã
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ngày (vd: Thứ 2 - day=1, Thứ 3 - day=2,...)
     */
    private Integer day;

    /**
     * Gian hàng
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Stall_id")
    private Stall stall;

    /**
     * Danh sách Món ăn
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Schedule_Dishes",
            joinColumns = @JoinColumn(name = "Schedule_id"),
            inverseJoinColumns = @JoinColumn(name = "Dishes_id")
    )
    private List<Dishes> dishesList;
}
