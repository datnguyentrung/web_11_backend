package com.dat.backend_version_2.controller.tournament;

import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeCombination;
import com.dat.backend_version_2.domain.tournament.TournamentMatch;
import com.dat.backend_version_2.dto.tournament.PoomsaeHistoryDTO;
import com.dat.backend_version_2.dto.tournament.TournamentMatchDTO;
import com.dat.backend_version_2.enums.tournament.TournamentType;
import com.dat.backend_version_2.mapper.tournament.TournamentMatchMapper;
import com.dat.backend_version_2.service.tournament.Poomsae.PoomsaeCombinationService;
import com.dat.backend_version_2.service.tournament.TournamentMatchService;
import com.dat.backend_version_2.util.error.IdInvalidException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tournament-matches")
public class TournamentMatchController {
    private final TournamentMatchService tournamentMatchService;
    private final PoomsaeCombinationService poomsaeCombinationService;

    // 🟢 Get all tournament matches
    @GetMapping
    public ResponseEntity<List<TournamentMatch>> getAllTournamentMatches() {
        try {
            List<TournamentMatch> matches = tournamentMatchService.getAllTournamentMatches();
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🟢 Get tournament match by composite key
    @PostMapping("/find")
    public ResponseEntity<TournamentMatch> getTournamentMatchById(@RequestBody TournamentMatchDTO.KeyInfo keyInfo) {
        try {
            TournamentMatch match = tournamentMatchService.getTournamentMatchById(keyInfo);
            return ResponseEntity.ok(match);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🟢 Create new tournament match
    @PostMapping
    public ResponseEntity<TournamentMatch> createTournamentMatch(@RequestBody TournamentMatchDTO tournamentMatchDTO) {
        try {
            TournamentMatch newMatch = tournamentMatchService.createTournamentMatch(tournamentMatchDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newMatch);
        } catch (IllegalArgumentException | IdInvalidException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🟢 Get tournament matches by tournament ID
    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<TournamentMatchDTO>> getTournamentMatchesByTournament(@PathVariable String tournamentId) {
        try {
            List<PoomsaeCombination> poomsaeCombinations = poomsaeCombinationService.getAllCombinations();

            Map<UUID, PoomsaeCombination> poomsaeCombinationMap = poomsaeCombinations.stream()
                    .collect(Collectors.toMap(
                            PoomsaeCombination::getIdPoomsaeCombination,
                            Function.identity()
                    ));

            List<TournamentMatchDTO> result = tournamentMatchService
                    .getTournamentMatchesByTournamentId(tournamentId)
                    .stream()
                    .map(match -> {
                        TournamentMatchDTO dto = TournamentMatchMapper.tournamentMatchToDTO(match);

                        // --- Gắn categoryName nếu là POOMSAE ---
                        if (dto.getMatchInfo().getTournamentType() == TournamentType.POOMSAE) {
                            try {
                                UUID comboId = UUID.fromString(dto.getKeyInfo().getIdCombination());
                                PoomsaeCombination combo = poomsaeCombinationMap.get(comboId);
                                if (combo != null && combo.getPoomsaeContent() != null) {
                                    dto.getMatchInfo().setCategoryName(
                                            new PoomsaeHistoryDTO.PoomsaeCategory(
                                                    combo.getAgeGroup().getAgeGroupName(),
                                                    combo.getBeltGroup().getBeltGroupName(),
                                                    combo.getPoomsaeContent().getContentName()
                                            )
                                    );
                                }
                            } catch (IllegalArgumentException ignored) {
                                // idCombination không hợp lệ → bỏ qua
                            }
                        }

                        return dto;
                    })
                    .toList();

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🟢 Get tournament matches by combination ID
    @GetMapping("/combination/{combinationId}")
    public ResponseEntity<List<TournamentMatch>> getTournamentMatchesByCombination(@PathVariable String combinationId) {
        try {
            List<TournamentMatch> allMatches = tournamentMatchService.getAllTournamentMatches();
            List<TournamentMatch> filteredMatches = allMatches.stream()
                    .filter(match -> match.getIdCombination().toString().equals(combinationId))
                    .toList();
            return ResponseEntity.ok(filteredMatches);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update-relations")
    public ResponseEntity<String> updateMatchRelations(@RequestBody TournamentMatchDTO tournamentMatchDTO) {
        try {
            tournamentMatchService.updateMatchRelations(tournamentMatchDTO);
            return ResponseEntity.ok("Match relations updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entity not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-relations")
    public ResponseEntity<String> deleteMatchRelations(@RequestBody TournamentMatchDTO.KeyInfo keyInfo) {
        return handleDeleteOperation(() -> tournamentMatchService.deleteMatch(keyInfo, false),
                "Match relations deleted successfully.");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteTournamentMatch(@RequestBody TournamentMatchDTO.KeyInfo keyInfo) {
        return handleDeleteOperation(() -> tournamentMatchService.deleteMatch(keyInfo, true),
                "Tournament match deleted successfully.");
    }

    // --- Hàm xử lý chung cho 2 endpoint ---
    private ResponseEntity<String> handleDeleteOperation(Runnable action, String successMessage) {
        try {
            action.run();
            return ResponseEntity.ok(successMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entity not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }
}
