package com.example.banthing.domain.item.service;

import com.example.banthing.domain.item.entity.ItemImg;
import com.example.banthing.domain.item.repository.ItemImgRepository;
import com.example.banthing.domain.item.repository.ItemRepository;
import com.example.banthing.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemImgService {

    private static final Logger logger = LoggerFactory.getLogger(ItemImgService.class);
    private final ItemImgRepository itemImgsRepository;
    private final ItemRepository itemRepository;
    private final S3Service s3Service;
    private String folderName = "itemImage";

    public void save(List<MultipartFile> images, Long itemId) throws IOException {
        List<ItemImg> itemImgs = new ArrayList<>();
        String itemFolderPath = folderName + "/" + itemId;

        for (MultipartFile image : images) {
            String imgUrl = s3Service.uploadImage(itemFolderPath, image);
            ItemImg itemImg = ItemImg.builder()
                    .imgUrl(itemId + "/" + imgUrl)
                    .item(itemRepository.findById(itemId).orElseThrow())
                    .build();
            itemImgs.add(itemImg);
        }
        itemImgsRepository.saveAll(itemImgs);
    }

    public void delete(Long itemId) {
        String itemFolderPath = folderName + "/" + itemId;
        s3Service.deleteFolder(itemFolderPath);
        List<ItemImg> itemImgs = itemImgsRepository.findByItemId(itemId);
        itemImgsRepository.deleteAll(itemImgs);
    }

    public void update(List<MultipartFile> newImages, Long itemId) throws IOException {
        logger.info("Updating images for item {}", itemId);
        delete(itemId);
        save(newImages, itemId);
        logger.info("Update successful for item {}", itemId);
    }

    public List<ItemImg> findItemImgs(Long itemId) {
        return itemImgsRepository.findByItemId(itemId);
    }

    public List<String> getBase64EncodedImages(Long itemId) {
        List<ItemImg> itemImgs = findItemImgs(itemId);
        String itemFolderPath = folderName + "/" + itemId;

        return itemImgs.stream()
                .map(itemImg -> {
                    try {
                        return s3Service.encodeImageToBase64(itemImg.getImgUrl(), itemFolderPath);
                    } catch (IOException e) {
                        logger.error("Failed to encode image to Base64: {}", itemImg.getImgUrl(), e);
                        return null;
                    }
                })
                .filter(base64 -> base64 != null)
                .collect(Collectors.toList());
    }

    public List<String> getImgNames(Long itemId) {
        return findItemImgs(itemId).stream()
                .map(ItemImg::getImgUrl)
                .collect(Collectors.toList());
    }
}
