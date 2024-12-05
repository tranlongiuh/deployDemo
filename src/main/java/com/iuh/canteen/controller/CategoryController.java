package com.iuh.canteen.controller;

import com.iuh.canteen.entity.Category;
import com.iuh.canteen.service.impl.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CategoryController Bộ điều khiển danh mục
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryServiceImpl categoryService;

    /**
     * getAllCategories Lấy tất cả các danh mục
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getAllCategories() {

        List<Category> result;
        try {
            result = categoryService.findAll();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    /**
     * getCategoryById Lấy Danh mục theo mã
     *
     * @param id Mã Danh mục
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {

        Category result;
        try {
            result = categoryService.findById(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    /**
     * createCategory Tạo Danh mục mới
     *
     * @param category Danh mục
     * @return
     */
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {

        Category result;
        try {
            result = categoryService.save(category);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }
}
