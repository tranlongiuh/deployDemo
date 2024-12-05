package com.iuh.canteen.service;

import com.iuh.canteen.entity.Image;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ImageService {

    public Image save(byte[] imageData, String contentType);

    public Image findById(Long id);

    public Boolean deleteImageById(Long id);

    public Image updateImage(Long id, byte[] imageData, String contentType);

    public List<Long> getAllId();
}
