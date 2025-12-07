package com.dat.backend_version_2.dto.tournament;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgeGroupDTO {
    private String ageGroupName;
    private Integer minAge;
    private Integer maxAge;
}
