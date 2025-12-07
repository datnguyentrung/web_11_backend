package com.dat.backend_version_2.controller.tournament.Poomsae;

import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeCombination;
import com.dat.backend_version_2.dto.tournament.PoomsaeCombinationDTO;
import com.dat.backend_version_2.mapper.tournament.PoomsaeCombinationMapper;
import com.dat.backend_version_2.service.tournament.Poomsae.PoomsaeCombinationService;
import com.dat.backend_version_2.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/poomsae-combinations")
public class PoomsaeCombinationController {
    private final PoomsaeCombinationService poomsaeCombinationService;

    @GetMapping
    public ResponseEntity<List<PoomsaeCombination>> getAllCombinations() {
        List<PoomsaeCombination> combinations = poomsaeCombinationService.getAllCombinations();
        return ResponseEntity.ok(combinations);
    }

    @PatchMapping("/change-mode")
    public ResponseEntity<String> changePoomsaeMode(
            @RequestBody PoomsaeCombinationDTO.ChangePoomsaeModeRequest request) {
        poomsaeCombinationService.changePoomsaeMode(request.getIdPoomsaeCombinations(), request.getPoomsaeMode());
        return ResponseEntity.ok("Poomsae mode updated successfully.");
    }

    @GetMapping("/tournament")
    public ResponseEntity<List<PoomsaeCombinationDTO.CombinationDetail>> getCombinationByIdTournament(
            @RequestParam String idTournament) throws IdInvalidException {
        return ResponseEntity.ok(poomsaeCombinationService.getCombinationByIdTournament(idTournament).stream()
                .map(PoomsaeCombinationMapper::poomsaeCombinationToCombinationDetail)
                .toList());
    }

    @PostMapping
    public ResponseEntity<String> createPoomsaeCombinations(
            @RequestBody PoomsaeCombinationDTO.CreateRequest requests
    ) throws IdInvalidException {
        poomsaeCombinationService.createPoomsaeCombinations(requests);
        return ResponseEntity.ok("Poomsae combinations created successfully.");
    }

    @DeleteMapping("/{id}")
    public void deletePoomsaeCombination(@PathVariable String id) {
        poomsaeCombinationService.deleteCombination(id);
    }
}
