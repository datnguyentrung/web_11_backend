package com.dat.backend_version_2.service.tournament;

import com.dat.backend_version_2.domain.tournament.Tournament;
import com.dat.backend_version_2.domain.tournament.TournamentMatch;
import com.dat.backend_version_2.dto.tournament.TournamentMatchDTO;
import com.dat.backend_version_2.dto.tournament.TournamentMatchId;
import com.dat.backend_version_2.enums.tournament.TournamentType;
import com.dat.backend_version_2.repository.tournament.Sparring.TournamentMatchRepository;
import com.dat.backend_version_2.service.tournament.Poomsae.PoomsaeCombinationService;
import com.dat.backend_version_2.util.error.IdInvalidException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentMatchService {
    private final TournamentMatchRepository tournamentMatchRepository;
    private final TournamentService tournamentService;
    private final PoomsaeCombinationService poomsaeCombinationService;

    public List<TournamentMatch> getAllTournamentMatches() {
        return tournamentMatchRepository.findAll();
    }

    public TournamentMatch getTournamentMatchById(TournamentMatchDTO.KeyInfo keyInfo) {
        if (keyInfo == null) {
            throw new IllegalArgumentException("KeyInfo must not be null");
        }

        TournamentMatchId tournamentMatchId = new TournamentMatchId(
                UUID.fromString(keyInfo.getTournament()),
                UUID.fromString(keyInfo.getIdCombination()),
                keyInfo.getTargetNode()
        );

        return tournamentMatchRepository.findById(tournamentMatchId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("TournamentMatch not found for tournament=%s, combination=%s, targetNode=%d",
                                keyInfo.getTournament(), keyInfo.getIdCombination(), keyInfo.getTargetNode())
                ));
    }

    public TournamentMatch createTournamentMatch(TournamentMatchDTO tournamentMatchDTO) throws IdInvalidException {
        if (tournamentMatchDTO == null || tournamentMatchDTO.getKeyInfo() == null || tournamentMatchDTO.getMatchInfo() == null) {
            throw new IllegalArgumentException("TournamentMatchDTO and its fields must not be null");
        }

        TournamentMatchDTO.KeyInfo keyInfo = tournamentMatchDTO.getKeyInfo();
        TournamentMatchDTO.MatchInfo matchInfo = tournamentMatchDTO.getMatchInfo();

        TournamentMatchId tournamentMatchId = new TournamentMatchId(
                UUID.fromString(keyInfo.getTournament()),
                UUID.fromString(keyInfo.getIdCombination()),
                keyInfo.getTargetNode()
        );

        if (tournamentMatchRepository.existsById(tournamentMatchId)) {
            throw new EntityExistsException(
                    String.format("TournamentMatch already exists for tournament=%s, combination=%s, targetNode=%d",
                            keyInfo.getTournament(), keyInfo.getIdCombination(), keyInfo.getTargetNode())
            );
        }

        Tournament tournament = tournamentService.getTournamentById(keyInfo.getTournament());
        if (tournament == null) {
            throw new IdInvalidException("Invalid tournament ID: " + keyInfo.getTournament());
        }

        TournamentMatch newMatch = new TournamentMatch();
        newMatch.setTournament(tournament);
        newMatch.setIdCombination(UUID.fromString(keyInfo.getIdCombination()));
        newMatch.setTargetNode(keyInfo.getTargetNode());
        newMatch.setTournamentType(matchInfo.getTournamentType());
        newMatch.setParticipants(keyInfo.getParticipants());

        if (matchInfo.getTournamentType() == TournamentType.POOMSAE) {
            String contentName = poomsaeCombinationService.getCombinationById(keyInfo.getIdCombination())
                    .getPoomsaeContent().getContentName();
            System.out.println("Content Name: " + contentName);

            if (contentName != null && (contentName.contains("TEAM") || contentName.contains("PAIR"))) {
                newMatch.setDuration(Duration.ofMinutes(15));
            } else {
                if (keyInfo.getTargetNode() == -1 || keyInfo.getTargetNode() == 0) {
                    newMatch.setDuration(Duration.ofMinutes(4));
                } else {
                    newMatch.setDuration(Duration.ofMinutes(3));
                }
            }
        } else if (matchInfo.getTournamentType() == TournamentType.SPARRING) {
            if (keyInfo.getTargetNode() == -1 || keyInfo.getTargetNode() == 0) {
                newMatch.setDuration(Duration.ofMinutes(10));
            } else {
                newMatch.setDuration(Duration.ofMinutes(4));
            }
        }

        return tournamentMatchRepository.save(newMatch);
    }

    public List<TournamentMatch> getTournamentMatchesByTournamentId(String tournamentId) throws IdInvalidException {
        UUID tournamentUUID;
        try {
            tournamentUUID = UUID.fromString(tournamentId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid tournament ID format: " + tournamentId);
        }

        Tournament tournament = tournamentService.getTournamentById(tournamentId);

        return tournamentMatchRepository.findAllByTournament(tournament);
    }

    @Transactional
    public void updateMatchRelations(TournamentMatchDTO dto) {
        TournamentMatch current = getTournamentMatchById(dto.getKeyInfo());

        // --- Ngắt khỏi vị trí cũ ---
        disconnectNeighbors(current);

        // Reset liên kết cũ của current
        current.setLeftMatch(null);
        current.setRightMatch(null);
        current.setSession(dto.getMatchInfo().getSession());

        // --- Gán vị trí mới ---
        TournamentMatch newLeft = null;
        TournamentMatch newRight = null;

        if (dto.getRelationInfo() != null) {
            if (dto.getRelationInfo().getLeftMatch() != null) {
                newLeft = getTournamentMatchById(dto.getRelationInfo().getLeftMatch());
            }
            if (dto.getRelationInfo().getRightMatch() != null) {
                newRight = getTournamentMatchById(dto.getRelationInfo().getRightMatch());
            }
        }

        // --- Cập nhật liên kết mới ---
        if (newLeft != null) {
            newLeft.setRightMatch(current);
            current.setLeftMatch(newLeft);
        }
        if (newRight != null) {
            newRight.setLeftMatch(current);
            current.setRightMatch(newRight);
        }

        // --- Không cần save() thủ công ---
        // @Transactional + Persistence Context sẽ tự dirty-check
    }

    @Transactional
    public void deleteMatch(TournamentMatchDTO.KeyInfo keyInfo, boolean removeEntity) {
        TournamentMatch toDelete = getTournamentMatchById(keyInfo);
        if (toDelete == null) {
            throw new EntityNotFoundException("Tournament match not found.");
        }

        // --- Ngắt liên kết với các trận đấu bên cạnh ---
        disconnectNeighbors(toDelete);

        toDelete.setLeftMatch(null);
        toDelete.setRightMatch(null);
        toDelete.setSession(null);

        // --- Nếu yêu cầu xóa luôn entity ---
        if (removeEntity) {
            tournamentMatchRepository.delete(toDelete);
        }
    }

    /**
     * Ngắt liên kết của node với các node hàng xóm.
     */
    private void disconnectNeighbors(TournamentMatch match) {
        TournamentMatch left = match.getLeftMatch();
        TournamentMatch right = match.getRightMatch();

        if (left != null) left.setRightMatch(right);
        if (right != null) right.setLeftMatch(left);
    }
}
