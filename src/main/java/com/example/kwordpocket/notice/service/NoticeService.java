package com.example.kwordpocket.notice.service;

import com.example.kwordpocket.notice.dto.CreateNoticeRequest;
import com.example.kwordpocket.notice.dto.CreateNoticeResponse;
import com.example.kwordpocket.notice.dto.GetNoticeResponse;
import com.example.kwordpocket.notice.dto.UpdateNoticeRequest;
import com.example.kwordpocket.notice.dto.UpdateNoticeResponse;
import com.example.kwordpocket.notice.entity.Notice;
import com.example.kwordpocket.notice.repository.NoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public Page<GetNoticeResponse> getAll(Pageable pageable) {
        return noticeRepository.findAll(pageable)
                .map(GetNoticeResponse::from);
    }

    public GetNoticeResponse getOne(Long id) {
        return GetNoticeResponse.from(findNoticeById(id));
    }

    @Transactional
    public CreateNoticeResponse create(CreateNoticeRequest request) {
        Notice notice = Notice.builder()
                .title(request.title())
                .content(request.content())
                .build();
        return CreateNoticeResponse.from(noticeRepository.save(notice));
    }

    @Transactional
    public UpdateNoticeResponse update(Long id, UpdateNoticeRequest request) {
        Notice notice = findNoticeById(id);
        notice.update(request.title(), request.content());
        noticeRepository.flush();
        return UpdateNoticeResponse.from(notice);
    }

    @Transactional
    public void delete(Long id) {
        Notice notice = findNoticeById(id);
        noticeRepository.delete(notice);
    }

    private Notice findNoticeById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다."));
    }
}
