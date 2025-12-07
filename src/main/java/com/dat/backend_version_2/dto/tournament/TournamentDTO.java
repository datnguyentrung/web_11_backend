package com.dat.backend_version_2.dto.tournament;

import com.dat.backend_version_2.enums.tournament.TournamentScope;
import com.dat.backend_version_2.enums.tournament.TournamentState;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
}
