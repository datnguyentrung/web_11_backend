package com.dat.backend_version_2.mapper.achievement;

import com.dat.backend_version_2.domain.achievement.PoomsaeList;
import com.dat.backend_version_2.dto.achievement.PoomsaeListDTO;
import com.dat.backend_version_2.mapper.training.StudentMapper;

public class PoomsaeListMapper {
    public static PoomsaeListDTO poomsaeListToDTO(PoomsaeList entity) {
        return new PoomsaeListDTO(
                entity.getIdPoomsaeList().toString(),
                poomsaeListToCompetitorDTO(entity)
        );
    }

    public static PoomsaeListDTO.CompetitorDTO poomsaeListToCompetitorDTO(PoomsaeList poomsaeList) {
        if (poomsaeList == null) return null;
        PoomsaeListDTO.CompetitorDTO competitorDTO = new PoomsaeListDTO.CompetitorDTO();
        competitorDTO.setCompetition(poomsaeListToCompetitionDTO(poomsaeList));
        competitorDTO.setMedal(poomsaeList.getMedal());
        competitorDTO.setIdAccount(poomsaeList.getStudent().getIdAccount());
        return competitorDTO;
    }

    public static PoomsaeListDTO.CompetitionDTO poomsaeListToCompetitionDTO(PoomsaeList poomsaeList) {
        if (poomsaeList == null) return null;
        PoomsaeListDTO.CompetitionDTO competitionDTO = new PoomsaeListDTO.CompetitionDTO();
        competitionDTO.setIdPoomsaeCombination(poomsaeList.getPoomsaeCombination().getIdPoomsaeCombination().toString());
        competitionDTO.setIdTournament(poomsaeList.getTournament().getIdTournament().toString());
        return competitionDTO;
    }
}

