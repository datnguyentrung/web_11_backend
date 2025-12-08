package com.dat.backend_version_2.service.tournament;

import com.dat.backend_version_2.domain.tournament.Tournament;
import com.dat.backend_version_2.dto.tournament.TournamentDTO;
import com.dat.backend_version_2.enums.tournament.TournamentState;
import com.dat.backend_version_2.mapper.tournament.TournamentMapper;
import com.dat.backend_version_2.redis.tournament.TournamentRedisImpl;
import com.dat.backend_version_2.repository.tournament.TournamentRepository;
import com.dat.backend_version_2.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Cacheable(value = "tournament", key = "#tournamentId")
    public Tournament getTournamentById(String tournamentId) throws IdInvalidException {
        return tournamentRepository.findById(UUID.fromString(tournamentId))
                    .orElseThrow(() -> new IdInvalidException("Tournament not found with id: " + tournamentId));
    }

    @CachePut(value = "tournament", key = "#response.idTournament")
    @Transactional
    public Tournament updateTournament(TournamentDTO.TournamentResponse response) {
        Tournament tournament = tournamentRepository.findById(UUID.fromString(response.getIdTournament()))
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id: " + response.getIdTournament()));

        TournamentMapper.tournamentInfoToTournament(response, tournament);

        return tournamentRepository.save(tournament);
    }

    @CacheEvict(value = "tournament", key = "#tournamentId")
    @Transactional
    public void deleteTournamentById(String tournamentId) throws IdInvalidException {
        if (!tournamentRepository.existsById(UUID.fromString(tournamentId))){
            throw new IdInvalidException("Tournament not found with id: " + tournamentId);
        }
        tournamentRepository.deleteById(UUID.fromString(tournamentId));
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
