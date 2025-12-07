package com.dat.backend_version_2.controller.tournament.Poomsae;

import com.dat.backend_version_2.dto.tournament.PoomsaeHistoryDTO;
import com.dat.backend_version_2.mapper.tournament.PoomsaeHistoryMapper;
import com.dat.backend_version_2.service.tournament.Poomsae.PoomsaeHistoryService;
import com.dat.backend_version_2.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/poomsae-histories")
public class PoomsaeHistoryController {

    private final PoomsaeHistoryService poomsaeHistoryService;

    // ========================================================================
    // 1. GENERAL READ OPERATIONS (Lấy dữ liệu)
    // ========================================================================

    @GetMapping
    public ResponseEntity<List<PoomsaeHistoryDTO>> getAllPoomsaeHistory() {
        return ResponseEntity.ok(poomsaeHistoryService.getAllPoomsaeHistory().stream()
                .map(PoomsaeHistoryMapper::poomsaeHistoryToPoomsaeHistoryDTO)
                .toList());
    }

    @GetMapping("/tournament/{idTournament}")
    public ResponseEntity<List<PoomsaeHistoryDTO>> getByIdTournament(@PathVariable String idTournament) throws IdInvalidException {
        return ResponseEntity.ok(poomsaeHistoryService.getAllPoomsaeHistoryByIdTournament(idTournament));
    }

    @GetMapping("/combination/{idPoomsaeConbination}")
    public ResponseEntity<List<PoomsaeHistoryDTO>> getByIdPoomsaeCombination(
            @PathVariable String idPoomsaeConbination) throws IdInvalidException {
        return ResponseEntity.ok(poomsaeHistoryService.getAllPoomsaeHistoryByIdPoomsaeCombination(idPoomsaeConbination));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkPoomsaeHistoryExists(
            @RequestParam String idTournament,
            @RequestParam(required = false) String idCombination,
            @RequestParam(required = false) String idAccount
    ) throws IdInvalidException {
        boolean exists = poomsaeHistoryService.checkPoomsaeHistoryExists(idTournament,idCombination, idAccount);
        return ResponseEntity.ok(exists);
    }

    // ========================================================================
    // 2. ELIMINATION MODE (Đấu loại trực tiếp)
    // ========================================================================

    @PostMapping("/elimination")
    public ResponseEntity<String> createPoomsaeHistoryForNode(@RequestBody List<String> poomsaeList) throws IdInvalidException {
        if (poomsaeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Poomsae list is empty");
        }
        System.out.println(poomsaeList);
        poomsaeHistoryService.createPoomsaeHistoryForNode(poomsaeList);
        return ResponseEntity.status(HttpStatus.CREATED).body("Poomsae history created");
    }

    @PostMapping("/elimination/winner")
    public ResponseEntity<String> createPoomsaeWinner(
            @RequestParam int participants,
            @RequestBody PoomsaeHistoryDTO poomsaeHistoryDTO
    ) throws IdInvalidException {
        String newNodeId = poomsaeHistoryService.createWinnerForElimination(poomsaeHistoryDTO, participants);
        return ResponseEntity.ok("Winner created successfully with new node ID: " + newNodeId);
    }

    @DeleteMapping("/elimination")
    public ResponseEntity<String> deletePoomsaeHistoryForElimination(
            @RequestParam int participants,
            @RequestParam String idPoomsaeHistory) throws IdInvalidException {

        poomsaeHistoryService.deletePoomsaeHistoryForElimination(participants, idPoomsaeHistory);
        return ResponseEntity.ok("Đã xóa PoomsaeHistory thành công (id = " + idPoomsaeHistory + ")");
    }

    // ========================================================================
    // 3. ROUND ROBIN MODE (Đấu vòng tròn)
    // ========================================================================

    @PostMapping("/round-robin")
    public ResponseEntity<String> createPoomsaeHistoryForRoundRobin(@RequestBody List<String> poomsaeList) throws IdInvalidException {
        if (poomsaeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Poomsae list is empty");
        }
        System.out.println(poomsaeList);
        poomsaeHistoryService.createPoomsaeHistoryForRoundRobin(poomsaeList);
        return ResponseEntity.status(HttpStatus.CREATED).body("Poomsae history created");
    }

    @PostMapping("/round-robin/winner")
    public ResponseEntity<String> createRoundRobinWinner(
            @RequestBody PoomsaeHistoryDTO poomsaeHistoryDTO
    ) throws IdInvalidException {
        poomsaeHistoryService.createWinnerForRoundRobin(poomsaeHistoryDTO);

        String message = String.format(
                "✅ Winner processed successfully for combination: %s",
                poomsaeHistoryDTO.getReferenceInfo().getName()
        );
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/round-robin")
    public ResponseEntity<String> deletePoomsaeHistoryForRoundRobin(
            @RequestParam String idPoomsaeHistory) throws IdInvalidException {

        poomsaeHistoryService.deletePoomsaeHistoryForRoundRobin(idPoomsaeHistory);
        return ResponseEntity.ok("Đã xóa PoomsaeHistory thành công (id = " + idPoomsaeHistory + ")");
    }

    // ========================================================================
    // 4. BULK / ADMIN OPERATIONS (Xử lý hàng loạt)
    // ========================================================================

    @DeleteMapping("/combination/{idPoomsaeCombination}")
    public ResponseEntity<String> deleteAllPoomsaeHistoryByIdPoomsaeCombination(
            @PathVariable String idPoomsaeCombination) throws IdInvalidException {
        poomsaeHistoryService.deleteAllPoomsaeHistoryByIdPoomsaeCombination(idPoomsaeCombination);
        return ResponseEntity.ok("All Poomsae histories deleted for combination id: " + idPoomsaeCombination);
    }
}