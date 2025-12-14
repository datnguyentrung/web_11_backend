package com.dat.backend_version_2.dto.achievement;

import com.dat.backend_version_2.dto.training.Student.StudentRes;
import com.dat.backend_version_2.enums.achievement.Medal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitorBaseDTO {
    private List<CompetitorDetailDTO> competitorDetailDTO;
    private CompetitionDTO competitionDTO;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitorInputDTO {
        private Set<String> idAccounts;
        private CompetitionDTO competition;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitorDetailDTO {
        private String idCompetitor;
        private StudentRes.PersonalInfo personalInfo;
        private Medal medal; // Giả sử Medal là một enum hoặc class
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitionDTO {
        private String idTournament;
        private String idCombination;
    }
}
