package com.dat.backend_version_2.dto.tournament;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PoomsaeHistoryDTO {
    private String idPoomsaeHistory;
    private TournamentDTO.NodeInfo nodeInfo;
    private ReferenceInfo referenceInfo;
    private Boolean hasWon;       // có chiến thắng hay không

    // ----------- Thông tin liên kết tới danh sách và tổ hợp Poomsae -----------
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReferenceInfo {
        private String poomsaeList;
        private String poomsaeCombination;
        private PoomsaeCategory poomsaeCategory;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PoomsaeCategory {
        private String ageGroupName;
        private String beltGroupName;
        private String contentName;
    }
}