package com.dat.backend_version_2.service.achievement;

import com.dat.backend_version_2.domain.achievement.PoomsaeList;
import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeCombination;
import com.dat.backend_version_2.domain.tournament.Tournament;
import com.dat.backend_version_2.domain.training.Student;
import com.dat.backend_version_2.dto.achievement.CompetitorBaseDTO;
import com.dat.backend_version_2.repository.achievement.PoomsaeListRepository;
import com.dat.backend_version_2.repository.training.StudentRepository;
import com.dat.backend_version_2.service.tournament.Poomsae.PoomsaeCombinationService;
import com.dat.backend_version_2.service.tournament.TournamentService;
import com.dat.backend_version_2.service.training.StudentService;
import com.dat.backend_version_2.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoomsaeListService {
    private final PoomsaeListRepository poomsaeListRepository;
    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final TournamentService tournamentService;
    private final PoomsaeCombinationService poomsaeCombinationService;

    public List<PoomsaeList> getAllPoomsaeList() {
        return poomsaeListRepository.findAll();
    }

    public PoomsaeList getPoomsaeListById(String idPoomsaeList) throws IdInvalidException {
        return poomsaeListRepository.findById(UUID.fromString(idPoomsaeList))
                .orElseThrow(() -> new IdInvalidException("PoomsaeList not found with id: " + idPoomsaeList));
    }

    public List<PoomsaeList> getPoomsaeListByIdTournament(String idTournament) throws IdInvalidException {
        Tournament tournament = tournamentService.getTournamentById(idTournament);
        return poomsaeListRepository.findByTournament(tournament);
    }

    public List<PoomsaeList> getPoomsaeListByFilter(
            String idTournament,
            String idPoomsaeCombination,
            String idAccount,
            Integer idBranch
    ) throws IdInvalidException {
        return poomsaeListRepository.findByFilter(
                UUID.fromString(idTournament),
                UUID.fromString(idPoomsaeCombination),
                idAccount != null ? studentService.getStudentByIdAccount(idAccount).getIdUser() : null,
                idBranch
        );
    }

    @Transactional
    public void createPoomsaeList(CompetitorBaseDTO.CompetitorInputDTO competitors) throws IdInvalidException {
        if (competitors == null) {
            throw new IdInvalidException("Competitor list is empty");
        }

        if (competitors.getIdAccounts() == null || competitors.getIdAccounts().isEmpty()) {
            throw new IdInvalidException("Competitor accounts list is empty");
        }

        if (competitors.getCompetition() == null) {
            throw new IdInvalidException("Competition information is missing");
        }

        String idTournament = competitors.getCompetition().getIdTournament();
        String idCombination = competitors.getCompetition().getIdCombination();

        if (idTournament == null || idTournament.isEmpty()) {
            throw new IdInvalidException("Tournament ID is missing");
        }

        if (idCombination == null || idCombination.isEmpty()) {
            throw new IdInvalidException("Combination ID is missing");
        }

        log.debug("Creating poomsae list for tournament: {} and combination: {}", idTournament, idCombination);

        Tournament tournament = tournamentService.getTournamentById(idTournament);
        PoomsaeCombination combination = poomsaeCombinationService.getCombinationById(idCombination);

        log.debug("Found {} accounts to process", competitors.getIdAccounts().size());

        List<Student> students = studentRepository.findAllByIdAccountIn(competitors.getIdAccounts());

        if (students.size() != competitors.getIdAccounts().size()){
            List<String> foundAccounts = students.stream()
                    .map(Student::getIdAccount)
                    .toList();
            List<String> missingAccounts = competitors.getIdAccounts().stream()
                    .filter(idAccount -> !foundAccounts.contains(idAccount))
                    .toList();
            throw new IdInvalidException("Students not found for accounts: " + String.join(", ", missingAccounts));
        }

        List<PoomsaeList> poomsaeLists = new ArrayList<>();

        for (Student student : students) {
            if (!student.getIsActive())
                throw new IdInvalidException("Student is not active: " + student.getIdAccount());

            // Build entity
            PoomsaeList entity = new PoomsaeList();
            entity.setStudent(student);
            entity.setTournament(tournament);
            entity.setPoomsaeCombination(combination);
            entity.setMedal(null);

            poomsaeLists.add(entity);
        }

        // Batch save
        poomsaeListRepository.saveAll(poomsaeLists);
        log.info("Successfully created {} poomsae list entries", poomsaeLists.size());
    }
}
