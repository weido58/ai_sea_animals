package com.marine.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.marine.entity.MarineSpecies;
import com.marine.service.MarineSpeciesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 海洋生物识别控制器
 */
@Controller
@RequestMapping("/marine")
@RequiredArgsConstructor
@Slf4j
public class MarineSpeciesController {

    private final MarineSpeciesService marineSpeciesService;

    /**
     * 首页 - 显示上传页面
     */
    @GetMapping({"", "/", "/index"})
    public String index() {
        return "index";
    }

    /**
     * 上传图片识别
     */
    @PostMapping("/upload")
    public String uploadImage(@RequestParam("imageFile") MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {
        try {
            if (imageFile.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "请选择要上传的图片");
                return "redirect:/marine/index";
            }

            log.info("开始处理图片上传: {}", imageFile.getOriginalFilename());

            // 识别并保存
            MarineSpecies result = marineSpeciesService.recognizeAndSave(imageFile);

            redirectAttributes.addFlashAttribute("success", "图片识别成功！");
            redirectAttributes.addFlashAttribute("speciesId", result.getId());

            return "redirect:/marine/detail/" + result.getId();

        } catch (Exception e) {
            log.error("图片识别失败", e);
            redirectAttributes.addFlashAttribute("error", "识别失败: " + e.getMessage());
            return "redirect:/marine/index";
        }
    }

    /**
     * AJAX上传接口
     */
    @PostMapping("/api/upload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadImageAjax(@RequestParam("imageFile") MultipartFile imageFile) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (imageFile.isEmpty()) {
                result.put("success", false);
                result.put("message", "请选择要上传的图片");
                return ResponseEntity.badRequest().body(result);
            }

            log.info("AJAX处理图片上传: {}", imageFile.getOriginalFilename());

            // 识别并保存
            MarineSpecies species = marineSpeciesService.recognizeAndSave(imageFile);

            result.put("success", true);
            result.put("message", "识别成功");
            result.put("data", species);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("AJAX图片识别失败", e);
            result.put("success", false);
            result.put("message", "识别失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 物种详情页面
     */
    @GetMapping("/detail/{id}")
    public String speciesDetail(@PathVariable Long id, Model model) {
        try {
            MarineSpecies species = marineSpeciesService.getSpeciesDetail(id);
            if (species == null) {
                model.addAttribute("error", "未找到该物种信息");
                return "error";
            }

            model.addAttribute("species", species);
            return "detail";

        } catch (Exception e) {
            log.error("获取物种详情失败", e);
            model.addAttribute("error", "获取详情失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 物种列表页面
     */
    @GetMapping("/list")
    public String speciesList(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String keyword,
                              Model model) {
        try {
            Page<MarineSpecies> speciesPage = marineSpeciesService.getSpeciesPage(page, size, keyword);

            model.addAttribute("speciesPage", speciesPage);
            model.addAttribute("keyword", keyword);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);

            return "list";

        } catch (Exception e) {
            log.error("获取物种列表失败", e);
            model.addAttribute("error", "获取列表失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * API - 获取物种列表
     */
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSpeciesListApi(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Map<String, Object> result = new HashMap<>();

        try {
            Page<MarineSpecies> speciesPage = marineSpeciesService.getSpeciesPage(page, size, keyword);

            result.put("success", true);
            result.put("data", speciesPage);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("API获取物种列表失败", e);
            result.put("success", false);
            result.put("message", "获取列表失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * API - 获取物种详情
     */
    @GetMapping("/api/detail/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSpeciesDetailApi(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        try {
            MarineSpecies species = marineSpeciesService.getSpeciesDetail(id);

            if (species == null) {
                result.put("success", false);
                result.put("message", "未找到该物种信息");
                return ResponseEntity.ok(result);
            }

            result.put("success", true);
            result.put("data", species);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("API获取物种详情失败", e);
            result.put("success", false);
            result.put("message", "获取详情失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 删除物种
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteSpecies(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = marineSpeciesService.deleteSpecies(id);

            if (success) {
                result.put("success", true);
                result.put("message", "删除成功");
            } else {
                result.put("success", false);
                result.put("message", "删除失败，未找到该记录");
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("删除物种失败", e);
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 获取所有物种（用于下拉选择等）
     */
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllSpecies() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<MarineSpecies> speciesList = marineSpeciesService.getAllSpecies();

            result.put("success", true);
            result.put("data", speciesList);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("获取所有物种失败", e);
            result.put("success", false);
            result.put("message", "获取数据失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 批量上传页面
     */
    @GetMapping("/batch")
    public String batchUpload() {
        return "batch";
    }

    /**
     * 统计信息API
     */
    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> result = new HashMap<>();

        try {
            long totalCount = marineSpeciesService.count();

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalSpecies", totalCount);

            result.put("success", true);
            result.put("data", statistics);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            result.put("success", false);
            result.put("message", "获取统计信息失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
}