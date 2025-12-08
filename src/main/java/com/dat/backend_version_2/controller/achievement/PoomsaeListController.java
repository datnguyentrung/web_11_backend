package com.dat.backend_version_2.controller.achievement;

import com.dat.backend_version_2.domain.achievement.PoomsaeList;
import com.dat.backend_version_2.dto.achievement.CompetitorBaseDTO;
import com.dat.backend_version_2.mapper.achievement.PoomsaeListMapper;
import com.dat.backend_version_2.service.achievement.PoomsaeListService;
import com.dat.backend_version_2.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/poomsae-lists")
public class PoomsaeListController {
    private final PoomsaeListService poomsaeListService;

    @GetMapping
    public ResponseEntity<List<CompetitorBaseDTO>> getAll() {
        List<CompetitorBaseDTO> result = poomsaeListService.getAllPoomsaeList()
                .stream()
                .map(PoomsaeListMapper::poomsaeListToCompetitorBaseDTO)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PoomsaeList> getById(@PathVariable String id
    ) throws IdInvalidException {
        return ResponseEntity.ok(poomsaeListService.getPoomsaeListById(id));
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody CompetitorBaseDTO.CompetitorInputDTO competitorDTOS) {
        try {
            poomsaeListService.createPoomsaeList(competitorDTOS);
            return ResponseEntity.ok("All Poomsae lists created successfully");
        } catch (IdInvalidException e) {
            log.error("IdInvalidException while creating Poomsae lists: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error while creating Poomsae lists", e);
            return ResponseEntity.internalServerError().body("Error while creating Poomsae lists: " + e.getMessage());
        }
    }

    @GetMapping("/tournament/{idTournament}")
    public ResponseEntity<List<CompetitorBaseDTO>> getByIdTournament(
            @PathVariable String idTournament) throws IdInvalidException {
        List<CompetitorBaseDTO> poomsaeListDTOS = poomsaeListService
                .getPoomsaeListByIdTournament(idTournament)
                .stream()
                .map(PoomsaeListMapper::poomsaeListToCompetitorBaseDTO)
                .toList();
        return ResponseEntity.ok(poomsaeListDTOS);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CompetitorBaseDTO>> getByFilter(
            @RequestParam String idTournament,
            @RequestParam String idCombination,
            @RequestParam(required = false) Integer idBranch,
            @RequestParam(required = false) String idAccount
    ) throws IdInvalidException {
        return ResponseEntity.ok(poomsaeListService.getPoomsaeListByFilter(idTournament, idCombination, idAccount, idBranch)
                .stream()
                .map(PoomsaeListMapper::poomsaeListToCompetitorBaseDTO)
                .toList());
    }
}
