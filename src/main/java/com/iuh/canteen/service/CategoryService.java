package com.iuh.canteen.service;

import com.iuh.canteen.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    // Lấy tất cả các category
    public List<Category> findAll();

    // Lấy category theo ID
    public Category findById(Long id);

    // Tạo mới category
    public Category save(Category category);
}
