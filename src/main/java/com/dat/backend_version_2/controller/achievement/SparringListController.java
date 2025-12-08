package com.dat.backend_version_2.controller.achievement;

import com.dat.backend_version_2.domain.achievement.SparringList;
import com.dat.backend_version_2.dto.achievement.CompetitorBaseDTO;
import com.dat.backend_version_2.mapper.achievement.SparringListMapper;
import com.dat.backend_version_2.service.achievement.SparringListService;
import com.dat.backend_version_2.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sparring-lists")
public class SparringListController {
    private final SparringListService sparringListService;

    @GetMapping
    public ResponseEntity<List<CompetitorBaseDTO>> getAll() {
        List<CompetitorBaseDTO> result = sparringListService.getAllSparringList()
                .stream()
                .map(SparringListMapper::sparringListToDTO)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SparringList> getById(@PathVariable String id
    ) throws IdInvalidException {
        return ResponseEntity.ok(sparringListService.getSparringListById(id));
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody CompetitorBaseDTO.CompetitorInputDTO competitorDTOS) {
        try {
            sparringListService.createSparringList(competitorDTOS);
            return ResponseEntity.ok("All Sparring lists created successfully");
        } catch (IdInvalidException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error while creating Sparring lists");
        }
    }

    @GetMapping("/tournament/{idTournament}")
    public ResponseEntity<List<CompetitorBaseDTO>> getByIdTournament(
            @PathVariable String idTournament) throws IdInvalidException {
        List<CompetitorBaseDTO> sparringListDTOS = sparringListService
                .getSparringListByIdTournament(idTournament)
                .stream()
                .map(SparringListMapper::sparringListToDTO)
                .toList();
        return ResponseEntity.ok(sparringListDTOS);
    }
}
