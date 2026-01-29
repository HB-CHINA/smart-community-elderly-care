package com.huangbin.smartcommunityelderlycare.controller;

import com.huangbin.smartcommunityelderlycare.common.Result;
import com.huangbin.smartcommunityelderlycare.entity.Notice;
import com.huangbin.smartcommunityelderlycare.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @PostMapping
    public Result<Notice> createNotice(@RequestBody Notice notice) {
        try {
            // 从登录信息中获取管理员ID，或者前端传递
            // 这里假设前端传递了adminId
            if (notice.getAdminId() == null) {
                return Result.error("管理员ID不能为空");
            }
            Notice created = noticeService.createNotice(notice);
            return Result.success("公告创建成功", created);
        } catch (Exception e) {
            return Result.error("创建失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Notice> updateNotice(@PathVariable Long id, @RequestBody Notice notice) {
        try {
            Notice updated = noticeService.updateNotice(id, notice);
            return Result.success("公告更新成功", updated);
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return Result.success("公告删除成功", null);
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Notice> getNoticeById(@PathVariable Long id) {
        try {
            Notice notice = noticeService.getNoticeById(id);
            return Result.success("查询成功", notice);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/published")
    public Result<List<Notice>> getPublishedNotices() {
        try {
            List<Notice> notices = noticeService.getPublishedNotices();
            return Result.success("查询成功", notices);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public Result<List<Notice>> getAllNotices() {
        try {
            List<Notice> notices = noticeService.getAllNotices();
            return Result.success("查询成功", notices);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}