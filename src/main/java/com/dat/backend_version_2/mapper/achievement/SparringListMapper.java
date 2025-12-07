package com.dat.backend_version_2.mapper.achievement;

import com.dat.backend_version_2.domain.achievement.SparringList;
import com.dat.backend_version_2.dto.achievement.CompetitorBaseDTO;
import com.dat.backend_version_2.mapper.training.StudentMapper;

import static com.dat.backend_version_2.mapper.training.StudentMapper.studentToPersonalAcademicInfo;

public class SparringListMapper {
    public static CompetitorBaseDTO sparringListToDTO(SparringList entity) {
        return new CompetitorBaseDTO(
                entity.getIdSparringList().toString(),
                sparringListToCompetitorDTO(entity)
        );
    }

    public static CompetitorBaseDTO.CompetitorDetailDTO sparringListToCompetitorDTO(SparringList sparringList) {
        if (sparringList == null) return null;
        CompetitorBaseDTO.CompetitorDetailDTO competitorDTO = new CompetitorBaseDTO.CompetitorDetailDTO();
        competitorDTO.setCompetition(sparringListToCompetitionDTO(sparringList));
        competitorDTO.setMedal(sparringList.getMedal());
        competitorDTO.setPersonalAcademicInfo(studentToPersonalAcademicInfo(sparringList.getStudent()));
        return competitorDTO;
    }

    public static CompetitorBaseDTO.CompetitionDTO sparringListToCompetitionDTO(SparringList sparringList) {
        if (sparringList == null) return null;
        CompetitorBaseDTO.CompetitionDTO competitionDTO = new CompetitorBaseDTO.CompetitionDTO();
        competitionDTO.setIdCombination(sparringList.getSparringCombination().getIdSparringCombination().toString());
        competitionDTO.setIdTournament(sparringList.getTournament().getIdTournament().toString());
        return competitionDTO;
    }
}

