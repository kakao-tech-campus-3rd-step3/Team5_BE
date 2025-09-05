package com.knuissant.dailyq.archive.controller;

import com.knuissant.dailyq.archive.dto.ArchiveResponse;
import com.knuissant.dailyq.archive.service.ArchiveService;
import lombok.RequiredArgsConstructor;
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

  @GetMapping("/{answerId}")
  public ResponseEntity<ArchiveResponse> getArchiveDetail(
      @RequestParam Long userId,
      @PathVariable Long answerId) {

    ArchiveResponse archiveDetail = archiveService.getArchiveDetail(userId, answerId);

    return ResponseEntity.ok(archiveDetail);
  }

}
