package com.dat.backend_version_2.controller.tournament.Poomsae;

import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeContent;
import com.dat.backend_version_2.service.tournament.Poomsae.PoomsaeContentService;
import com.dat.backend_version_2.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/poomsae-contents")
public class PoomsaeContentController {

    private final PoomsaeContentService poomsaeContentService;

    // 🟢 Lấy danh sách tất cả nội dung thi đấu
    @GetMapping
    public ResponseEntity<List<PoomsaeContent>> getAllPoomsaeContents() {
        List<PoomsaeContent> contents = poomsaeContentService.getAllPoomsaeContent();
        return ResponseEntity.ok(contents);
    }

    // 🟢 Lấy nội dung thi đấu theo ID
    @GetMapping("/{id}")
    public ResponseEntity<PoomsaeContent> getPoomsaeContentById(@PathVariable Integer id) throws IdInvalidException {
        PoomsaeContent content = poomsaeContentService.getPoomsaeContentById(id);
        return ResponseEntity.ok(content);
    }

    // 🟡 Tạo mới nội dung thi đấu (và tự động tạo combinations)
    @PostMapping
    public ResponseEntity<PoomsaeContent> createPoomsaeContent(@RequestParam String contentName) {
        PoomsaeContent newContent = poomsaeContentService.createPoomsaeContent(contentName);
        return ResponseEntity.status(HttpStatus.CREATED).body(newContent);
    }

    // 🟠 Cập nhật nội dung thi đấu
    @PutMapping
    public ResponseEntity<PoomsaeContent> updatePoomsaeContent(@RequestBody PoomsaeContent poomsaeContent) {
        PoomsaeContent updated = poomsaeContentService.updatePoomsaeContent(poomsaeContent);
        return ResponseEntity.ok(updated);
    }

    // 🔴 Xóa nội dung thi đấu theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoomsaeContent(@PathVariable Integer id) {
        poomsaeContentService.deletePoomsaeContent(id);
        return ResponseEntity.noContent().build();
    }
}
