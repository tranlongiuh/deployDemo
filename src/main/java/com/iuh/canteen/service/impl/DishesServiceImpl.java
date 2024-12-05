package com.iuh.canteen.service.impl;

import com.iuh.canteen.dto.DishesDTO;
import com.iuh.canteen.entity.*;
import com.iuh.canteen.repository.CategoryRepository;
import com.iuh.canteen.repository.DishesRepository;
import com.iuh.canteen.service.DishesService;
import com.iuh.canteen.service.ImageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DishesServiceImpl implements DishesService {

    @Autowired
    private DishesRepository dishesRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private StallServiceImpl stallService;

    @Autowired
    private ScheduleServiceImpl scheduleService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * create Khởi tạo Món ăn mới
     *
     * @param dishesDTO  Dữ liệu món ăn
     * @param file       File hình ảnh
     * @param username   Tên Tài khoản Khởi tạo
     * @param categoryId Mã Danh mục
     * @return DishesDTO
     */
    @Override
    public DishesDTO create(DishesDTO dishesDTO, MultipartFile file, String username,
                            Long categoryId) {

        DishesDTO result;
        try {
            Long idUser = userService.getIdByUsername(username);
            Stall stall = stallService.findByManagerId(idUser);
            Category category = categoryRepository.findById(categoryId)
                                                  .orElseThrow(() -> new
                                                          RuntimeException("Category not found"));
            // Xử lý ảnh
            if (file != null && !file.isEmpty()) {
                byte[] imageData = file.getBytes();
                String contentType = file.getContentType();
                Image savedImage = imageService.save(imageData, contentType);
                // Set URL ảnh vào FoodDto
                System.err.println("savedImage.getId() " + savedImage.getId());
                dishesDTO.setImageId(savedImage.getId());
            }
            Dishes dishes = modelMapper.map(dishesDTO, Dishes.class);
            dishes.setCategory(category);
            dishes.setStall(stall);
            dishesRepository.save(dishes);
            result = modelMapper.map(dishes, DishesDTO.class);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * getAll Lấy toàn bộ Món ăn
     *
     * @return List DishesDTO
     */
    @Override
    public List<DishesDTO> getAll() {

        List<DishesDTO> result = new ArrayList<>();
        try {
            dishesRepository.findAll()
                            .forEach(
                                    dishes -> result.add(modelMapper.map(dishes, DishesDTO.class))
                            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * getAllForUser Lấy toàn bộ Món ăn dựa trênq quyền của Tài khoản
     *
     * @param username Tên Tài khoản
     * @return List DishesDTO
     */
    @Override
    public List<DishesDTO> getAllForUser(String username) {

        List<DishesDTO> result = new ArrayList<>();
        try {
            Long managerId = userService.getIdByUsername(username);
            Stall stall = stallService.findByManagerId(managerId);
            dishesRepository.findByStallId(stall.getId())
                            .forEach(
                                    dishes ->
                                            result.add(modelMapper.map(dishes, DishesDTO.class))
                            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * findById Tìm Món ăn theo Mã Món ăn
     *
     * @param id Mã Món ăn
     * @return DishesDTO
     */
    @Override
    public DishesDTO findById(Long id) {

        DishesDTO result;
        try {
            result = modelMapper.map(dishesRepository.findById(id)
                                                     .orElse(null), DishesDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * update Cập nhật món ăn
     *
     * @param id         Mã Món ăn
     * @param dishesDTO  Dữ liệu món ăn
     * @param categoryId Mã Danh mục
     * @param image      File Hình ảnh
     * @param username   Tên Tài khoản thực hiện
     * @return DishesDTO
     */
    @Override
    public DishesDTO update(Long id, DishesDTO dishesDTO, Long categoryId, MultipartFile image, String username) {

        Dishes result = new Dishes();
        try {
            Long idManager = userService.getIdByUsername(username);
            Stall stall = stallService.findByManagerId(idManager);
            System.out.println(stall);
            if (stall.getDishes()
                     .stream()
                     .anyMatch(dishes -> dishes.getId()
                                               .equals(id))) {
                result = dishesRepository.findById(id)
                                         .orElse(null);
                if (image != null && !image.isEmpty()) {
                    if (result.getImageId() != null) {
                        byte[] imageData = image.getBytes();
                        String contentType = image.getContentType();
                        Image updateImage = imageService.updateImage(result.getImageId(), imageData, contentType);
                        dishesDTO.setImageId(updateImage.getId());
                    } else {
                        byte[] imageData = image.getBytes();
                        String contentType = image.getContentType();
                        Image savedImage = imageService.save(imageData, contentType);
                        dishesDTO.setImageId(savedImage.getId());
                    }
                    result.setImageId(dishesDTO.getImageId());
                }
                // Cập nhật thông tin món ăn
                result.setName(dishesDTO.getName());
                result.setDescription(dishesDTO.getDescription());
                result.setPrice(dishesDTO.getPrice());
                result.setCategory(categoryRepository.findById(categoryId)
                                                     .orElse(null));
                dishesRepository.save(result);
            } else {
                System.err.println("not found dishes in your stall");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return modelMapper.map(result, DishesDTO.class);
    }

    /**
     * delete Xóa Món ăn
     *
     * @param id Mã Món ăn
     * @return true - xóa thành công
     */
    @Override
    public Boolean delete(Long id) {

        boolean result = false;
        try {
            // Check if the dish exists and get the associated image
            Optional<Dishes> optionalDish = dishesRepository.findById(id);
            if (optionalDish.isPresent()) {
                Dishes dish = optionalDish.get();
                // Remove image if it exists
                if (dish.getImageId() != null) {
                    imageService.deleteImageById(dish.getImageId());
                }
                // Find all schedules containing the dish and remove the dish from the schedules
                List<Schedule> schedules = scheduleService.findByDishesId(id);
                for (Schedule schedule : schedules) {
                    schedule.getDishesList()
                            .removeIf(dishes -> dishes.getId()
                                                      .equals(id));
                    scheduleService.save(schedule);  // Save the updated schedule
                }
                // Delete the dish itself
                dishesRepository.deleteById(id);
                result = true;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting dish: " + e.getMessage(), e);
        }
        return result;
    }

    /**
     * save Lưu Món ăn
     *
     * @param dishesDTO
     * @return DishesDTO
     */
    @Override
    public DishesDTO save(DishesDTO dishesDTO) {

        DishesDTO result;
        try {
            Dishes dishes = modelMapper.map(dishesDTO, Dishes.class);
            dishesRepository.save(dishes);
            result = modelMapper.map(dishesDTO, DishesDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * subtractQuantity Trừ số lượng Món ăn còn lại trong Gian hàng
     *
     * @param id       Mã Món ăn
     * @param quantity Số lượng giảm
     * @return true - Trừ thành công
     */
    @Override
    public Boolean subtractQuantity(Long id, Integer quantity) {

        boolean result = false;
        try {
            Dishes dishes = dishesRepository.findById(id)
                                            .orElseThrow();
            dishes.setQuantity(dishes.getQuantity() - quantity);
            dishesRepository.save(dishes);
            result = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
