package com.dat.backend_version_2.dto.tournament;

import com.dat.backend_version_2.enums.tournament.TournamentType;
import com.dat.backend_version_2.enums.training.ClassSession.Session;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentMatchDTO {
    private KeyInfo keyInfo;
    private MatchInfo matchInfo;
    private RelationInfo relationInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KeyInfo {
        private String tournament;
        private String idCombination;
        private Integer targetNode;
        private Integer participants;
        private boolean isFirstNode;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MatchInfo {
        private PoomsaeHistoryDTO.PoomsaeCategory categoryName;
        private TournamentType tournamentType;
        private Duration duration;
        private Session session;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelationInfo {
        private KeyInfo leftMatch;
        private KeyInfo rightMatch;
    }
}
