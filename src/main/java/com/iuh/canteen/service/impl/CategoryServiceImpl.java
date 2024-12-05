package com.iuh.canteen.service.impl;

import com.iuh.canteen.entity.Category;
import com.iuh.canteen.repository.CategoryRepository;
import com.iuh.canteen.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * findAll Lấy toàn bộ Danh mục Thức ăn
     *
     * @return List Category
     */
    @Override
    public List<Category> findAll() {

        return categoryRepository.findAll();
    }

    /**
     * findById Lấy Danh mục Thức ăn theo Mã Danh mục
     *
     * @param id Mã Danh mục
     * @return Category
     */
    @Override
    public Category findById(Long id) {

        Optional<Category> category = categoryRepository.findById(id);
        return category.orElseThrow(() -> new RuntimeException("Category not found"));
    }

    /**
     * save Lưu Danh mục
     *
     * @param category Danh mục
     * @return Category
     */
    @Override
    public Category save(Category category) {

        return categoryRepository.save(category);
    }
}
