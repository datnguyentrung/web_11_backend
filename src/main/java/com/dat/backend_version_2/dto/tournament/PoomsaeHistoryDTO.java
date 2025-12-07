package com.dat.backend_version_2.dto.tournament;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PoomsaeHistoryDTO {
    private String idPoomsaeHistory;
    private NodeInfo nodeInfo;
    private ReferenceInfo referenceInfo;
    private Boolean hasWon;       // có chiến thắng hay không

    // ----------- Thông tin node (vị trí trong cây) -----------
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NodeInfo {
        private Integer sourceNode;   // node gốc
        private Integer targetNode;   // node mục tiêu
        private Integer levelNode;    // cấp độ node
    }

    // ----------- Thông tin liên kết tới danh sách và tổ hợp Poomsae -----------
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReferenceInfo {
        private String name;
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