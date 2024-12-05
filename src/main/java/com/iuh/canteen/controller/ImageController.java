package com.iuh.canteen.controller;

import com.iuh.canteen.entity.Image;
import com.iuh.canteen.service.impl.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * ImageController Bộ điều khiển hình ảnh
 */
@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageServiceImpl imageService;

    /**
     * uploadImage Tạo hình ảnh mới
     *
     * @param file File Hình ảnh
     * @return
     */
    @PostMapping("/upload")
    public ResponseEntity<Image> uploadImage(@RequestParam("image") MultipartFile file) {

        try {
            byte[] imageData = file.getBytes();
            String contentType = file.getContentType();
            Image savedImage = imageService.save(imageData, contentType);
            return new ResponseEntity<>(savedImage, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * getImage Lấy Hình ảnh theo Mã Hình ảnh
     *
     * @param id Mã Hình ảnh
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {

        Image result;
        try {
            result = imageService.findById(id);
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", result.getContentType());
                return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    /**
     * getImages Lấy danh sách Mã Hình ảnh
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getImages() {

        List<Long> result;
        try {
            result = imageService.getAllId();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    /**
     * deleteImage Xóa Hình ảnh theo Mã Hình ảnh
     *
     * @param id Mã Hình ảnh
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {

        try {
            if (imageService.deleteImageById(id)) {
                return ResponseEntity.ok()
                                     .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    /**
     * Cập nhật Hình ảnh theo Mã Hình ảnh
     *
     * @param id   Mã Hình ảnh
     * @param file File Hình ảnh
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Image> updateImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file) {

        try {
            byte[] imageData = file.getBytes();
            String contentType = file.getContentType();
            Image updatedImage = imageService.updateImage(id, imageData, contentType);
            return updatedImage != null ?
                    new ResponseEntity<>(updatedImage, HttpStatus.OK) :
                    new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
