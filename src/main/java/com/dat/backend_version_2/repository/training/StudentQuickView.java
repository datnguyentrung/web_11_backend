package com.dat.backend_version_2.repository.training;

public interface StudentQuickView {
    // --- Personal Info ---
    String getName();
    String getIdAccount();
    String getIdNational();
    String getBirthDate();
    Boolean getIsActive();

    // --- Academic Info ---
    Integer getIdBranch();
    String getBeltLevel();
    // Vì Native Query trả về dòng phẳng, ta sẽ gom các lớp học thành chuỗi "Lớp A,Lớp B"
    String getClassSessions();
}
