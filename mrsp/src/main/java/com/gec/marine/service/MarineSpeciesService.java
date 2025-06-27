package com.gec.marine.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gec.marine.dto.QwenRecognitionDTO;
import com.gec.marine.entity.MarineSpecies;
import com.gec.marine.mapper.MarineSpeciesMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 海洋生物业务逻辑服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MarineSpeciesService extends ServiceImpl<MarineSpeciesMapper, MarineSpecies> {

    private final QwenVisionService qwenVisionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 图片存储路径
    private static final String IMAGE_UPLOAD_PATH = "uploads/images/";

    /**
     * 通过图片识别并保存海洋生物信息
     */
    @Transactional
    public MarineSpecies recognizeAndSave(MultipartFile imageFile) throws IOException {
        // 1. 验证文件
        validateImageFile(imageFile);

        // 2. 保存图片文件
        String imagePath = saveImageFile(imageFile);

        try {
            // 3. 调用千问API识别
            QwenRecognitionDTO recognition = qwenVisionService.recognizeMarineSpecies(imageFile);
            log.info("识别结果: {}", recognition);

            // 4. 转换为实体对象
            MarineSpecies marineSpecies = convertToEntity(recognition, imagePath);

            // 5. 保存到数据库
            save(marineSpecies);

            log.info("成功保存海洋生物信息: {}", marineSpecies.getId());
            return marineSpecies;

        } catch (Exception e) {
            // 识别失败时删除已保存的图片
            deleteImageFile(imagePath);
            throw new RuntimeException("识别并保存海洋生物信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 分页查询海洋生物
     */
    public Page<MarineSpecies> getSpeciesPage(int pageNum, int pageSize, String keyword) {
        Page<MarineSpecies> page = new Page<>(pageNum, pageSize);
        QueryWrapper<MarineSpecies> wrapper = new QueryWrapper<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                    .like("scientific_name", keyword)
                    .or()
                    .like("common_name", keyword)
                    .or()
                    .like("chinese_name", keyword)
            );
        }

        wrapper.orderByDesc("created_time");
        return page(page, wrapper);
    }

    /**
     * 根据学名查询是否已存在
     */
    public boolean existsByScientificName(String scientificName) {
        QueryWrapper<MarineSpecies> wrapper = new QueryWrapper<>();
        wrapper.eq("scientific_name", scientificName);
        return count(wrapper) > 0;
    }

    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("请选择要上传的图片");
        }
        // 检查文件大小 (10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("图片文件大小不能超过10MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        List<String> allowedTypes = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif");
        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("只支持JPG、PNG、GIF格式的图片");
        }
    }

    /**
     * 保存图片文件
     */
    private String saveImageFile(MultipartFile file) throws IOException {
        // 创建上传目录
        File uploadDir = new File(IMAGE_UPLOAD_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;
        String filePath = IMAGE_UPLOAD_PATH + fileName;
        System.out.println("filePath="+filePath);
        // 保存文件
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());

        log.info("图片保存成功: {}", filePath);
        return filePath;
    }

    /**
     * 删除图片文件
     */
    private void deleteImageFile(String imagePath) {
        try {
            Files.deleteIfExists(Paths.get(imagePath));
            log.info("删除图片文件: {}", imagePath);
        } catch (IOException e) {
            log.error("删除图片文件失败: {}", imagePath, e);
        }
    }

    /**
     * 转换识别结果为实体对象
     */
    private MarineSpecies convertToEntity(QwenRecognitionDTO recognition, String imagePath) {
        try {
            MarineSpecies species = new MarineSpecies();
            species.setScientificName(recognition.getScientificName());
            species.setCommonName(recognition.getCommonName());
            species.setChineseName(recognition.getChineseName());
            species.setHabitat(recognition.getHabitat());
            species.setDistribution(recognition.getDistribution());
            species.setCharacteristics(recognition.getCharacteristics());
            species.setSizeRange(recognition.getSizeRange());
            species.setDiet(recognition.getDiet());
            species.setConservationStatus(recognition.getConservationStatus());
            species.setDescription(recognition.getDescription());
            species.setCreatedTime(LocalDateTime.now());
            species.setUpdatedTime(LocalDateTime.now());
            species.setIsDeleted(0);

            // 转换分类信息为JSON
            if (recognition.getClassification() != null) {
                String classificationJson = objectMapper.writeValueAsString(recognition.getClassification());
                species.setClassification(classificationJson);
            }

            // 设置图片URL
            species.setImageUrls(objectMapper.writeValueAsString(Arrays.asList(imagePath)));

            return species;
        } catch (Exception e) {
            throw new RuntimeException("转换识别结果失败", e);
        }
    }

    /**
     * 获取所有物种列表
     */
    public List<MarineSpecies> getAllSpecies() {
        QueryWrapper<MarineSpecies> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_time");
        return list(wrapper);
    }

    /**
     * 根据ID获取详细信息
     */
    public MarineSpecies getSpeciesDetail(Long id) {
        return getById(id);
    }

    /**
     * 删除物种信息
     */
    @Transactional
    public boolean deleteSpecies(Long id) {
        MarineSpecies species = getById(id);
        if (species != null) {
            // 删除关联的图片文件
            try {
                if (species.getImageUrls() != null) {
                    String[] imageUrls = objectMapper.readValue(species.getImageUrls(), String[].class);
                    for (String imageUrl : imageUrls) {
                        deleteImageFile(imageUrl);
                    }
                }
            } catch (Exception e) {
                log.error("删除图片文件失败", e);
            }

            return removeById(id);
        }
        return false;
    }
}
