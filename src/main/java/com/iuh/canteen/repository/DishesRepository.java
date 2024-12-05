package com.iuh.canteen.repository;

import com.iuh.canteen.entity.Category;
import com.iuh.canteen.entity.Dishes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishesRepository extends JpaRepository<Dishes, Long> {

    List<Dishes> findByNameContainingIgnoreCase(String name);

    List<Dishes> findByCategory(Category category);

    List<Dishes> findByStallId(Long id);
    // Thêm các phương thức tìm kiếm và lọc khác nếu cần
}
