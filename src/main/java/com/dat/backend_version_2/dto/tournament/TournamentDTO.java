package com.dat.backend_version_2.dto.tournament;

import com.dat.backend_version_2.dto.training.Student.StudentRes;
import com.dat.backend_version_2.enums.tournament.TournamentScope;
import com.dat.backend_version_2.enums.tournament.TournamentState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
public class TournamentDTO {

    @Data
    public static class TournamentInfo {
        private String tournamentName;
        private LocalDate tournamentDate;
        private String location;
        private TournamentScope tournamentScope;
        private TournamentState tournamentState;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class TournamentResponse extends TournamentInfo {
        private String idTournament;
    }

    // ----------- Thông tin node (vị trí trong cây) -----------
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NodeInfo {
        private Integer sourceNode;   // node gốc
        private Integer targetNode;   // node mục tiêu
        private Integer levelNode;    // cấp độ node
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HistoryInfo extends NodeInfo {
        private String idHistory;
        private Boolean hasWon;
        private StudentRes.PersonalInfo student;
    }
}
