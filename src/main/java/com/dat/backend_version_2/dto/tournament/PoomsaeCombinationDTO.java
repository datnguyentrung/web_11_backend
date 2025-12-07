package com.dat.backend_version_2.dto.tournament;

import com.dat.backend_version_2.enums.tournament.PoomsaeMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class PoomsaeCombinationDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePoomsaeModeRequest {
        private List<String> idPoomsaeCombinations;
        private PoomsaeMode poomsaeMode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private List<Integer> idPoomsaeContent;
        private List<Integer> idBeltGroup;
        private List<Integer> idAgeGroup;
        private String idTournament;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CombinationDetail {
        private String idPoomsaeCombination;
        private String idTournament;
        private String poomsaeContentName;
        private BeltGroupDTO beltGroupDTO;
        private AgeGroupDTO ageGroupDTO;
        private PoomsaeMode poomsaeMode;
    }
}
