package com.example.banthing.domain.item.service;

import com.example.banthing.domain.item.entity.ItemImg;
import com.example.banthing.domain.item.repository.ItemImgRepository;
import com.example.banthing.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemImgService {

    private final ItemImgRepository itemImgsRepository;
    private final ItemRepository itemRepository;

    @Value("${file.upload-dir}") //이미지 저장 경로
    private String uploadDir;

    public void save(List<MultipartFile> images, Long itemId) throws IOException {
        List<ItemImg> itemImgs = new ArrayList<>();
        for (MultipartFile image : images) {
            String imgUrl = saveImage(image, itemId);
            ItemImg itemImg = ItemImg.builder()
                    .imgUrl(imgUrl)
                    .item(itemRepository.findById(itemId).orElseThrow())
                    .build();
            itemImgs.add(itemImg);
        }
        itemImgsRepository.saveAll(itemImgs);
    }

    public String saveImage(MultipartFile image, Long itemId) throws IOException {

        String originalFilename = image.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

        File itemDir = new File(uploadDir + File.separator + itemId);

        if (!itemDir.exists()) {
            itemDir.mkdirs();
        }

        File[] files = itemDir.listFiles();
        int imageCount = (files != null) ? files.length + 1 : 1; // 기존 이미지 파일 개수 + 1
        String newFileName = imageCount + fileExtension;

        // 서버에 파일 저장
        File serverFile = new File(itemDir + File.separator + newFileName);
        image.transferTo(serverFile);

        return itemId + "/" + newFileName;
    }

    public void delete(Long itemId) {
        List<ItemImg> itemImgs = itemImgsRepository.findByItemId(itemId);
        for (ItemImg img : itemImgs) {
            deleteImage(img.getImgUrl());
        }
        itemImgsRepository.deleteAll(itemImgs);
    }

    private void deleteImage(String imgUrl) {
        File file = new File(uploadDir + File.separator + imgUrl);

        if (file.exists() && !file.delete()) {
            throw new RuntimeException("Failed to delete file: " + file.getAbsolutePath());
        }
    }

    public void update(List<MultipartFile> newImages, Long itemId) throws IOException {
        delete(itemId);
        save(newImages, itemId);
    }

    public List<ItemImg> findItemImgs(Long itemId) {
        return itemImgsRepository.findByItemId(itemId);
    }

}
