package com.huangbin.smartcommunityelderlycare.service;

import com.huangbin.smartcommunityelderlycare.entity.Notice;
import java.util.List;

public interface NoticeService {
    Notice createNotice(Notice notice);
    Notice updateNotice(Long noticeId, Notice notice);
    void deleteNotice(Long noticeId);
    Notice getNoticeById(Long noticeId);
    List<Notice> getPublishedNotices();
    List<Notice> getNoticesByPublisher(Long publisherId);
    List<Notice> getAllNotices();
}