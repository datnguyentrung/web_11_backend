package com.dat.backend_version_2.mapper.tournament;

import com.dat.backend_version_2.domain.tournament.Sparring.SparringHistory;
import com.dat.backend_version_2.dto.tournament.SparringHistoryDTO;
import com.dat.backend_version_2.dto.tournament.TournamentDTO;

public class SparringHistoryMapper {
    public static SparringHistoryDTO sparringHistoryToSparringHistoryDTO(SparringHistory sparringHistory) {
        if (sparringHistory == null) return null;
        SparringHistoryDTO dto = new SparringHistoryDTO();
        dto.setIdSparringHistory(String.valueOf(sparringHistory.getIdSparringHistory()));
        dto.setReferenceInfo(sparringHistoryToReferenceInfo(sparringHistory));
        dto.setNodeInfo(sparringHistoryToNodeInfo(sparringHistory));
        dto.setHasWon(sparringHistory.getHasWon());

        return dto;
    }

    public static SparringHistoryDTO.ReferenceInfo sparringHistoryToReferenceInfo(SparringHistory sparringHistory) {
        if (sparringHistory == null) return null;

        var referenceInfo = new SparringHistoryDTO.ReferenceInfo();
        var sparringList = sparringHistory.getSparringList();

        if (sparringList != null) {
            var student = sparringList.getStudent();
            var combination = sparringList.getSparringCombination();

            referenceInfo.setName(student != null ? student.getName() : null);
            referenceInfo.setSparringList(String.valueOf(sparringList.getIdSparringList()));
            referenceInfo.setSparringCombination(
                    combination != null ? String.valueOf(combination.getIdSparringCombination()) : null
            );
            referenceInfo.setSparringCategory(sparringHistoryToSparringCategory(sparringHistory));
        }

        return referenceInfo;
    }

    public static SparringHistoryDTO.SparringCategory sparringHistoryToSparringCategory(SparringHistory sparringHistory) {
        if (sparringHistory == null) return null;

        var combinationCategory = new SparringHistoryDTO.SparringCategory();
        var sparringList = sparringHistory.getSparringList();

        if (sparringList != null) {
            var combination = sparringList.getSparringCombination();
            if (combination != null) {
                combinationCategory.setAgeGroupName(combination.getAgeGroup().getAgeGroupName());
                combinationCategory.setGender(combination.getGender());
                combinationCategory.setWeightClass(combination.getSparringContent().getWeightClass());
            }
        }
        return combinationCategory;
    }

    public static TournamentDTO.NodeInfo sparringHistoryToNodeInfo(SparringHistory sparringHistory) {
        if (sparringHistory == null) return null;
        TournamentDTO.NodeInfo nodeInfo = new TournamentDTO.NodeInfo();
        nodeInfo.setTargetNode(sparringHistory.getTargetNode());
        nodeInfo.setSourceNode(sparringHistory.getSourceNode());
        nodeInfo.setLevelNode(sparringHistory.getLevelNode());
        return nodeInfo;
    }
}
