package com.dat.backend_version_2.dto.achievement;

import com.dat.backend_version_2.dto.training.Student.StudentRes;
import com.dat.backend_version_2.enums.achievement.Medal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoomsaeListDTO {
    private String idPoomsaeList;
    private CompetitorDTO competitor;

//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class CompetitorDTO {
//        private StudentRes.PersonalAcademicInfo personalAcademicInfo;
//        private Medal medal;
//        private CompetitionDTO competition;
//    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitorDTO {
        private String idAccount;
        private Medal medal;
        private CompetitionDTO competition;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitionDTO {
        private String idTournament;
        private String idPoomsaeCombination;
    }
}
