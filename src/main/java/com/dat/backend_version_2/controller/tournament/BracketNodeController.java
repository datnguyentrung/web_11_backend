package com.dat.backend_version_2.controller.tournament;

import com.dat.backend_version_2.dto.tournament.BracketNodeReq;
import com.dat.backend_version_2.service.tournament.BracketNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bracket-nodes")
public class BracketNodeController {
    private final BracketNodeService bracketNodeService;

    @PostMapping("/participants")
    public ResponseEntity<String> createSigma(@RequestBody Integer participants) {
        bracketNodeService.createBracketNodeAllowParticipants(participants);
        return ResponseEntity.ok("Created sigma with " + participants + " participants");
    }

    @GetMapping("/participants/{participants}")
    public ResponseEntity<List<BracketNodeReq.BracketInfo>> getAllBracketsByParticipant(@PathVariable Integer participants) {
        return ResponseEntity.ok(bracketNodeService.getBracketNodeByParticipants(participants));
    }
}
