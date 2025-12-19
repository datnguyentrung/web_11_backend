package com.dat.backend_version_2.controller.tournament.Poomsae;

import com.dat.backend_version_2.dto.tournament.PoomsaeCombinationDTO;
import com.dat.backend_version_2.dto.tournament.PoomsaeHistoryDTO;
import com.dat.backend_version_2.dto.tournament.TournamentDTO;
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

    @GetMapping("/filter")
    public ResponseEntity<List<TournamentDTO.HistoryInfo>> getPoomsaeHistoryByFilter(
            @RequestParam String idTournament,
            @RequestParam String idCombination,
            @RequestParam(required = false) String idAccount
    ) throws IdInvalidException {
        return ResponseEntity.ok(poomsaeHistoryService.getPoomsaeHistoryByFilter(idTournament, idCombination, idAccount));
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

    @GetMapping("/check-existence")
    public ResponseEntity<PoomsaeCombinationDTO.CheckModeResponse> checkExistence(
            @RequestParam String idTournament,
            @RequestParam(required = false) String idCombination,
            @RequestParam(required = false) String idAccount
    ) throws IdInvalidException {
        return ResponseEntity.ok(poomsaeHistoryService.checkPoomsaeMode(idTournament,idCombination, idAccount));
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

    @PostMapping("/elimination/winner/{idHistory}")
    public ResponseEntity<String> createPoomsaeWinner(
            @PathVariable String idHistory
    ) throws IdInvalidException {
        poomsaeHistoryService.createWinnerForElimination(idHistory);
        return ResponseEntity.ok("Winner created successfully");
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

//    @PostMapping("/round-robin/winner")
//    public ResponseEntity<String> createRoundRobinWinner(
//            @RequestBody PoomsaeHistoryDTO poomsaeHistoryDTO
//    ) throws IdInvalidException {
//        poomsaeHistoryService.createWinnerForRoundRobin(poomsaeHistoryDTO);
//        return ResponseEntity.ok("Winner created successfully");
//    }

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