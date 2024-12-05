package com.iuh.canteen.service.impl;

import com.iuh.canteen.entity.Image;
import com.iuh.canteen.repository.ImageRepository;
import com.iuh.canteen.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    /**
     * save Lưu Hình ảnh
     *
     * @param imageData   Dữ liệu hình ảnh
     * @param contentType Phân loại
     * @return Image
     */
    @Override
    public Image save(byte[] imageData, String contentType) {

        Image image = new Image();
        image.setData(imageData);
        image.setContentType(contentType);
        return imageRepository.save(image);
    }

    /**
     * findById Lấy Hình ảnh theo Mã Hình ảnh
     *
     * @param id Mã Hình ảnh
     * @return Image
     */
    @Override
    public Image findById(Long id) {

        return imageRepository.findById(id)
                              .orElse(null);
    }

    /**
     * deleteImageById Xóa Hình ảnh theo Mã Hình ảnh
     *
     * @param id Mã Hình ảnh
     * @return true - xóa thành công
     */
    @Override
    public Boolean deleteImageById(Long id) {

        try {
            imageRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * updateImage Cập nhật Hình ảnh
     *
     * @param id          Mã Hình ảnh
     * @param imageData   Dữ liệu hình ảnh
     * @param contentType Phân loại
     * @return Image
     */
    @Override
    public Image updateImage(Long id, byte[] imageData, String contentType) {

        Optional<Image> existingImage = imageRepository.findById(id);
        if (existingImage.isPresent()) {
            Image image = existingImage.get();
            image.setData(imageData);
            image.setContentType(contentType);
            return imageRepository.save(image);
        }
        return null;
    }

    /**
     * getAllId Lấy toàn bộ Mã Hình ảnh
     *
     * @return List Long
     */
    @Override
    public List<Long> getAllId() {

        return imageRepository.findAll()
                              .stream()
                              .map(Image::getId)
                              .collect(Collectors.toList());
    }
}
