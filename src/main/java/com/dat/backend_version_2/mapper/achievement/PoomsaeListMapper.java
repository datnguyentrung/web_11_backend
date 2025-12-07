package com.dat.backend_version_2.mapper.achievement;

import com.dat.backend_version_2.domain.achievement.PoomsaeList;
import com.dat.backend_version_2.domain.training.Student;
import com.dat.backend_version_2.dto.achievement.CompetitorBaseDTO;
import com.dat.backend_version_2.dto.training.Student.StudentRes;

import java.util.Collections;

public class PoomsaeListMapper {
    public static CompetitorBaseDTO poomsaeListToCompetitorBaseDTO(PoomsaeList entity) {
        return new CompetitorBaseDTO(
                entity.getIdPoomsaeList().toString(),
                poomsaeListToCompetitorDTO(entity)
        );
    }

    public static CompetitorBaseDTO.CompetitorDetailDTO poomsaeListToCompetitorDTO(PoomsaeList poomsaeList) {
        if (poomsaeList == null) return null;
        CompetitorBaseDTO.CompetitorDetailDTO competitorDTO = new CompetitorBaseDTO.CompetitorDetailDTO();
        competitorDTO.setCompetition(poomsaeListToCompetitionDTO(poomsaeList));
        competitorDTO.setMedal(poomsaeList.getMedal());
        competitorDTO.setPersonalAcademicInfo(studentToPersonalAcademicInfoWithoutClassSessions(poomsaeList.getStudent()));
        return competitorDTO;
    }

    public static CompetitorBaseDTO.CompetitionDTO poomsaeListToCompetitionDTO(PoomsaeList poomsaeList) {
        if (poomsaeList == null) return null;
        CompetitorBaseDTO.CompetitionDTO competitionDTO = new CompetitorBaseDTO.CompetitionDTO();
        competitionDTO.setIdCombination(poomsaeList.getPoomsaeCombination().getIdPoomsaeCombination().toString());
        competitionDTO.setIdTournament(poomsaeList.getTournament().getIdTournament().toString());
        return competitionDTO;
    }

    /**
     * Convert Student to PersonalAcademicInfo WITHOUT loading studentClassSessions
     * to avoid N+1 query problem
     */
    private static StudentRes.PersonalAcademicInfo studentToPersonalAcademicInfoWithoutClassSessions(Student student) {
        if (student == null) return null;

        StudentRes.PersonalInfo personalInfo = new StudentRes.PersonalInfo(
                student.getName(),
                student.getIdAccount(),
                student.getIdNational(),
                student.getBirthDate(),
                student.getIsActive()
        );

        StudentRes.AcademicInfo academicInfo = new StudentRes.AcademicInfo(
                student.getBranch().getIdBranch(),
                student.getBeltLevel(),
                Collections.emptyList() // Don't load classSessions to avoid N+1 query
        );

        return new StudentRes.PersonalAcademicInfo(personalInfo, academicInfo);
    }
}
