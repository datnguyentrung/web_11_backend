package com.dat.backend_version_2.mapper.achievement;

import com.dat.backend_version_2.domain.achievement.PoomsaeList;
import com.dat.backend_version_2.domain.achievement.SparringList;
import com.dat.backend_version_2.dto.achievement.CompetitorBaseDTO;
import com.dat.backend_version_2.mapper.training.StudentMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StudentMapper.class})
public interface CompetitorMapper {
    // Map từ Entity sang DTO chi tiết
    @Mapping(target = "idCompetitor", source = "entity.idPoomsaeList")
    @Mapping(target = "personalInfo", source = "entity.student")
    @Mapping(target = "medal", source = "entity.medal")
    CompetitorBaseDTO.CompetitorDetailDTO poomsaeListToCompetitorDTO(PoomsaeList entity);

    @Mapping(target = "idCompetitor", source = "entity.idSparringList")
    @Mapping(target = "personalInfo", source = "entity.student")
    @Mapping(target = "medal", source = "entity.medal")
    CompetitorBaseDTO.CompetitorDetailDTO sparringListToCompetitorDTO(SparringList entity);
}
