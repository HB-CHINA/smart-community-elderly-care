package com.huangbin.smartcommunityelderlycare.service.impl;

import com.huangbin.smartcommunityelderlycare.entity.Notice;
import com.huangbin.smartcommunityelderlycare.repository.NoticeRepository;
import com.huangbin.smartcommunityelderlycare.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Override
    public Notice createNotice(Notice notice) {
        // 不需要设置时间，因为 Notice 实体中已有默认值
        return noticeRepository.save(notice);
    }

    @Override
    public Notice updateNotice(Long noticeId, Notice notice) {
        Notice existing = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        existing.setTitle(notice.getTitle());
        existing.setContent(notice.getContent());
        existing.setStatus(notice.getStatus());
        return noticeRepository.save(existing);
    }

    @Override
    public void deleteNotice(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }

    @Override
    public Notice getNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
    }

    @Override
    public List<Notice> getPublishedNotices() {
        return noticeRepository.findByStatusOrderByPublishedAtDesc(1); // 1:已发布
    }

    @Override
    public List<Notice> getNoticesByPublisher(Long adminId) {  // 参数名改为 adminId
        return noticeRepository.findByAdminId(adminId);  // 调用新方法
    }

    @Override
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }
}