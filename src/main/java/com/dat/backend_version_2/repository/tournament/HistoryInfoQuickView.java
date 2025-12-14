package com.dat.backend_version_2.repository.tournament;

import java.time.LocalDate;
import java.util.UUID;

public interface HistoryInfoQuickView {
    UUID getIdHistory();

    Boolean getHasWon();

    // -- Node Info --
    Integer getSourceNode();

    Integer getTargetNode();

    Integer getLevelNode();

    // -- Student Info --
    String getStudentName();

//    String getAccountId();

    String getNationalId();

    LocalDate getBirthDate();

    Boolean getIsActiveStudent();
}
