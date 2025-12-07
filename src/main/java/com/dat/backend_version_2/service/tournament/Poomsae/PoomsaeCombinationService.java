package com.dat.backend_version_2.service.tournament.Poomsae;

import com.dat.backend_version_2.domain.tournament.AgeGroup;
import com.dat.backend_version_2.domain.tournament.BeltGroup;
import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeCombination;
import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeContent;
import com.dat.backend_version_2.domain.tournament.Tournament;
import com.dat.backend_version_2.dto.tournament.PoomsaeCombinationDTO;
import com.dat.backend_version_2.enums.tournament.PoomsaeMode;
import com.dat.backend_version_2.repository.tournament.Poomsae.PoomsaeCombinationRepository;
import com.dat.backend_version_2.service.tournament.AgeGroupService;
import com.dat.backend_version_2.service.tournament.BeltGroupService;
import com.dat.backend_version_2.service.tournament.TournamentService;
import com.dat.backend_version_2.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PoomsaeCombinationService {
    private final PoomsaeCombinationRepository poomsaeCombinationRepository;
    private final PoomsaeContentService poomsaeContentService;
    private final BeltGroupService beltGroupService;
    private final AgeGroupService ageGroupService;
    private final TournamentService tournamentService;

    @Transactional(readOnly = true)  // Giữ session active cho đến khi response được serialize
    public List<PoomsaeCombination> getAllCombinations() {
        return poomsaeCombinationRepository.findAllWithGraph();
    }

    @Transactional(readOnly = true)
    public PoomsaeCombination getCombinationById(String id) throws IdInvalidException {
        return poomsaeCombinationRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IdInvalidException("Poomsae combination not found with id: " + id));
    }

    @Transactional
    public void deleteCombination(String id) {
        poomsaeCombinationRepository.deleteById(UUID.fromString(id));
    }

    @Transactional
    public void changePoomsaeMode(List<String> ids, PoomsaeMode mode) {
        List<PoomsaeCombination> combinations = poomsaeCombinationRepository.findAllById(
                ids.stream().map(UUID::fromString).toList()
        );
        for (PoomsaeCombination combination : combinations) {
            combination.setPoomsaeMode(mode);
        }
        poomsaeCombinationRepository.saveAll(combinations);
    }

    @Transactional
    public void createPoomsaeCombinations(PoomsaeCombinationDTO.CreateRequest requests) throws IdInvalidException {
        poomsaeCombinationRepository.bulkInsertCombinations(
                UUID.fromString(requests.getIdTournament()),
                requests.getIdPoomsaeContent(),
                requests.getIdBeltGroup(),
                requests.getIdAgeGroup()
        );
    }

    public List<PoomsaeCombination> getCombinationByIdTournament(String idTournament) throws IdInvalidException {
        return poomsaeCombinationRepository.findByTournament(
                tournamentService.getTournamentById(idTournament)
        );
    }
}
