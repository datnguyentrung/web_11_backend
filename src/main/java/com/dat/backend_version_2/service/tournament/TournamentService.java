package com.dat.backend_version_2.service.tournament;

import com.dat.backend_version_2.domain.tournament.Tournament;
import com.dat.backend_version_2.dto.tournament.TournamentDTO;
import com.dat.backend_version_2.enums.tournament.TournamentState;
import com.dat.backend_version_2.mapper.tournament.TournamentMapper;
import com.dat.backend_version_2.repository.tournament.TournamentRepository;
import com.dat.backend_version_2.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    public Tournament getTournamentById(String tournamentId) throws IdInvalidException {
        return tournamentRepository.findById(UUID.fromString(tournamentId))
                .orElseThrow(() -> new IdInvalidException("Tournament not found with id: " + tournamentId));
    }

    public List<Tournament> getTournamentsByTournamentState(TournamentState state) throws IdInvalidException {
        return tournamentRepository.findByTournamentState(state);
    }

    public Tournament getTournamentByNameAndTournamentDate(String tournamentName, LocalDate tournamentDate) throws IdInvalidException {
        return tournamentRepository.findByTournamentNameAndTournamentDate(tournamentName, tournamentDate);
    }

    public void createNewTournament(TournamentDTO.TournamentInfo tournamentInfo) throws IdInvalidException {
        // Kiểm tra trùng tên + ngày tổ chức
        Optional<Tournament> existingTournament = Optional.ofNullable(
                getTournamentByNameAndTournamentDate(
                        tournamentInfo.getTournamentName(),
                        tournamentInfo.getTournamentDate()
                )
        );

        if (existingTournament.isPresent()) {
            throw new IdInvalidException(String.format(
                    "Tournament '%s' on date %s already exists",
                    tournamentInfo.getTournamentName(),
                    tournamentInfo.getTournamentDate()
            ));
        }

        // Tạo mới
        Tournament newTournament = new Tournament();
        TournamentMapper.tournamentInfoToTournament(tournamentInfo, newTournament);

        tournamentRepository.save(newTournament);
    }
}
