package com.iuh.canteen.service;

import com.iuh.canteen.dto.DishesDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface DishesService {

    // Create Food
    DishesDTO create(DishesDTO dishesDTO, MultipartFile file, String username,
                     Long categoryId);

    // Get All Foods
    List<DishesDTO> getAll();

    List<DishesDTO> getAllForUser(String username);

    // Get Food by ID
    DishesDTO findById(Long id);

    // Update Food by ID
    DishesDTO update(Long id, DishesDTO dishesDTO, Long categoryId, MultipartFile image, String username);

    // Delete Food by ID
    Boolean delete(Long id);

    DishesDTO save(DishesDTO dishesDTO);

    Boolean subtractQuantity(Long id, Integer quantity);
}
