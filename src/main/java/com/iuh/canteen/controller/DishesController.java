package com.iuh.canteen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iuh.canteen.dto.DishesDTO;
import com.iuh.canteen.security.JwtUtil;
import com.iuh.canteen.service.impl.DishesServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * DishesController Bộ điều khiển món ăn
 */
@RestController
@RequestMapping("/api/foods")
public class DishesController {

    @Autowired
    private DishesServiceImpl dishesService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * createFood Tạo món mới
     *
     * @param foodJson   Thông tin món ăn dạng Json
     * @param image      Dữ liệu hình ảnh
     * @param request
     * @param categoryId Mã Danh mục
     * @return
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createFood(
            @RequestParam("food") String foodJson,
            @RequestParam(value = "image", required = false) MultipartFile image, HttpServletRequest request,
            @RequestParam("categoryId") Long categoryId
    ) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            ObjectMapper objectMapper = new ObjectMapper();
            DishesDTO foodDto = objectMapper.readValue(foodJson, DishesDTO.class);
            dishesService.create(foodDto, image, username, categoryId);
            return ResponseEntity.status(HttpStatus.CREATED)
                                 .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .build();
    }

    /**
     * getAllFoods Lấy danh sách tất cả Món ăn
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getAllFoods() {

        List<DishesDTO> result;
        try {
            result = dishesService.getAll();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .build();
    }

    /**
     * getAllFoodsInStall Lấy danh sách tất cả Món ăn trong Gian hàng
     *
     * @param request
     * @return
     */
    @GetMapping("/manager")
    public ResponseEntity<?> getAllFoodsInStall(HttpServletRequest request) {

        List<DishesDTO> result;
        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            result = dishesService.getAllForUser(username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .build();
    }

    /**
     * getFoodById Lấy Món ăn theo Mã Món ăn
     *
     * @param id Mã Món ăn
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFoodById(@PathVariable Long id) {

        DishesDTO result;
        try {
            result = dishesService.findById(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .build();
    }

    /**
     * updateFood Cập nhật món ăn
     *
     * @param id         Mã Món ăn
     * @param foodJson   Thông tin Món ăn dạng Json
     * @param image      Hình ảnh Món ăn
     * @param categoryId Mã Danh mục
     * @param request
     * @return
     * @throws IOException
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFood(
            @PathVariable Long id,
            @RequestParam("food") String foodJson,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("categoryId") Long categoryId, HttpServletRequest request) throws IOException {

        DishesDTO result;
        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            ObjectMapper objectMapper = new ObjectMapper();
            DishesDTO dishesDTO = objectMapper.readValue(foodJson, DishesDTO.class);
            System.out.println(dishesDTO);
            result = dishesService.update(id, dishesDTO, categoryId, image, username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .build();
    }

    /**
     * deleteFood Xóa Món ăn theo Mã Món ăn
     *
     * @param id Mã Món ăn
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {

        try {
            if (dishesService.delete(id)) {
                return ResponseEntity.ok()
                                     .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .build();
    }
}
