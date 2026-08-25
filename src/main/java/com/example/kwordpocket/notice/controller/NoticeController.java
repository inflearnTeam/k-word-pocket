package com.example.kwordpocket.notice.controller;

import com.example.kwordpocket.notice.dto.CreateNoticeRequest;
import com.example.kwordpocket.notice.dto.CreateNoticeResponse;
import com.example.kwordpocket.notice.dto.GetNoticeResponse;
import com.example.kwordpocket.notice.dto.UpdateNoticeRequest;
import com.example.kwordpocket.notice.dto.UpdateNoticeResponse;
import com.example.kwordpocket.notice.service.NoticeService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<PagedModel<GetNoticeResponse>> getAll(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(new PagedModel<>(noticeService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetNoticeResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getOne(id));
    }

    @PostMapping
    public ResponseEntity<CreateNoticeResponse> create(@Valid @RequestBody CreateNoticeRequest request) {
        CreateNoticeResponse response = noticeService.create(request);
        return ResponseEntity
                .created(URI.create("/notices/" + response.id()))
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateNoticeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNoticeRequest request
    ) {
        return ResponseEntity.ok(noticeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
