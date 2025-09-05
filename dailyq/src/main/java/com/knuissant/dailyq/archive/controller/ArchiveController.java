package com.knuissant.dailyq.archive.controller;

import com.knuissant.dailyq.archive.dto.ArchiveResponse;
import com.knuissant.dailyq.archive.dto.ArchiveResponse.Summary;
import com.knuissant.dailyq.archive.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/archive")
public class ArchiveController {

  private final ArchiveService archiveService;

  @GetMapping
  public ResponseEntity<Page<ArchiveResponse.Summary>> getMyArchive(
      @RequestParam Long userId,
      @PageableDefault(size = 10, sort = "answeredAt", direction = Sort.Direction.DESC) Pageable pageable) {

    Page<ArchiveResponse.Summary> archiveList = archiveService.getArchiveList(userId, pageable);
    return ResponseEntity.ok(archiveList);
  }

  @GetMapping("/{answerId}")
  public ResponseEntity<ArchiveResponse.Detail> getArchiveDetail(
      @RequestParam Long userId,
      @PathVariable Long answerId) {

    ArchiveResponse.Detail archiveDetail = archiveService.getArchiveDetail(userId, answerId);

    return ResponseEntity.ok(archiveDetail);
  }

}
