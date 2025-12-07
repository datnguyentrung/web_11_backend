package com.dat.backend_version_2.dto.tournament;

import com.dat.backend_version_2.enums.training.BeltLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BeltGroupDTO {
    private String beltGroupName;
    private BeltLevel startBelt;
    private BeltLevel endBelt;
}
