package com.dat.backend_version_2.mapper.training;

import com.dat.backend_version_2.domain.training.Student;
import com.dat.backend_version_2.domain.training.StudentClassSession;
import com.dat.backend_version_2.dto.authentication.UserRes;
import com.dat.backend_version_2.dto.training.Student.StudentReq;
import com.dat.backend_version_2.dto.training.Student.StudentRes;
import com.dat.backend_version_2.enums.training.BeltLevel;
import com.dat.backend_version_2.repository.training.StudentQuickView;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.*;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "name", source = "personalInfo.name")
    @Mapping(target = "idUser", source = "personalInfo.idUser", qualifiedByName = "stringToUUID")
    @Mapping(target = "idNational", source = "personalInfo.idNational")
    @Mapping(target = "birthDate", source = "personalInfo.birthDate")
    void personalInfoToStudent(StudentReq.PersonalInfo personalInfo, @MappingTarget Student student);

    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "member", source = "contactInfo.member")
    void contactInfoToStudent(StudentReq.ContactInfo contactInfo, @MappingTarget Student student);

    @Mapping(target = "startDate", source = "enrollmentInfo.startDate", defaultExpression = "java(java.time.LocalDate.now())")
    @Mapping(target = "endDate", source = "enrollmentInfo.endDate")
    void enrollmentInfoToStudent(StudentReq.EnrollmentInfo enrollmentInfo, @MappingTarget Student student);

    @Mapping(target = "name", source = "student.name")
    @Mapping(target = "idAccount", source = "student.idAccount")
    @Mapping(target = "idNational", source = "student.idNational")
    @Mapping(target = "birthDate", source = "student.birthDate")
    @Mapping(target = "isActive", source = "student.isActive")
    StudentRes.PersonalInfo studentToPersonalInfo(Student student);

    @Mapping(target = "idBranch", source = "student.branch.idBranch")
    @Mapping(target = "beltLevel", source = "student.beltLevel")
    @Mapping(target = "classSessions", source = "student.studentClassSessions", qualifiedByName = "studentClassSessionsToIds")
    StudentRes.AcademicInfo studentToAcademicInfo(Student student);

    @Mapping(target = "personalInfo", source = "student")
    @Mapping(target = "academicInfo", source = "student")
    StudentRes.PersonalAcademicInfo studentToPersonalAcademicInfo(Student student);

    @Mapping(target = "idUser", source = "student.idUser")
    @Mapping(target = "idAccount", source = "student.idAccount")
    @Mapping(target = "email", source = "student.email")
    @Mapping(target = "idRole", source = "student.role.idRole")
    UserRes.UserInfo studentToUserInfo(Student student);

    @Mapping(target = "birthDate", source = "student.birthDate")
    @Mapping(target = "isActive", source = "student.isActive")
    @Mapping(target = "name", source = "student.name")
    @Mapping(target = "phone", source = "student.phone")
    @Mapping(target = "beltLevel", source = "student.beltLevel")
    UserRes.UserProfile studentToUserProfile(Student student);

    @Mapping(target = "userInfo", source = "student")
    @Mapping(target = "userProfile", source = "student")
    UserRes studentToUserRes(Student student);

    @Mapping(target = "name", source = "view.name")
    @Mapping(target = "idAccount", source = "view.idAccount")
    @Mapping(target = "idNational", source = "view.idNational")
    @Mapping(target = "birthDate", source = "view.birthDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "isActive", source = "view.isActive")
    StudentRes.PersonalInfo studentQuickViewToPersonalInfo(StudentQuickView view);

    @Mapping(target = "idBranch", source = "view.idBranch")
    @Mapping(target = "beltLevel", source = "view.beltLevel", qualifiedByName = "stringToBeltLevel")
    @Mapping(target = "classSessions", source = "view.classSessions", qualifiedByName = "stringToClassSessionList")
    StudentRes.AcademicInfo studentQuickViewToAcademicInfo(StudentQuickView view);

    @Mapping(target = "personalInfo", source = "view")
    @Mapping(target = "academicInfo", source = "view")
    StudentRes.PersonalAcademicInfo studentQuickViewToPersonalAcademicInfo(StudentQuickView view);

    /**
     * Convert Student to PersonalAcademicInfo WITHOUT loading studentClassSessions
     * to avoid N+1 query problem
     */
    @NoClassSessions
    @Mapping(target = "personalInfo", source = "student")
    @Mapping(target = "academicInfo", source = "student", qualifiedByName = "studentToAcademicInfoNoClassSessions")
    StudentRes.PersonalAcademicInfo studentToPersonalAcademicInfoWithoutClassSessions(Student student);
    /**
     * Convert Student to AcademicInfo WITHOUT loading studentClassSessions
     * to avoid N+1 query problem
     */
    @Named("studentToAcademicInfoNoClassSessions")
    @Mapping(target = "idBranch", source = "branch.idBranch")
    @Mapping(target = "beltLevel", source = "beltLevel")
    @Mapping(target = "classSessions", expression = "java(java.util.Collections.emptyList())")
    StudentRes.AcademicInfo toAcademicInfoNoClassSessions(Student student);

    // Named mapping methods
    @Named("stringToUUID")
    default UUID stringToUUID(String idUserStr) {
        if (idUserStr != null && !idUserStr.isBlank()) {
            try {
                return UUID.fromString(idUserStr);
            } catch (IllegalArgumentException e) {
                // Nếu chuỗi không phải UUID hợp lệ thì giữ null
                return null;
            }
        }
        return null;
    }

    @Named("stringToLocalDate")
    default LocalDate stringToLocalDate(String dateStr) {
        if (dateStr != null && !dateStr.isBlank()) {
            return LocalDate.parse(dateStr);
        }
        return null;
    }

    @Named("stringToBeltLevel")
    default BeltLevel stringToBeltLevel(String beltLevelStr) {
        if (beltLevelStr != null && !beltLevelStr.isBlank()) {
            return BeltLevel.valueOf(beltLevelStr);
        }
        return null;
    }

    @Named("stringToClassSessionList")
    default List<String> stringToClassSessionList(String classSessionsStr) {
        if (classSessionsStr != null && !classSessionsStr.isBlank()) {
            String[] sessionsArray = classSessionsStr.split(",");
            List<String> classSessions = new ArrayList<>();
            for (String session : sessionsArray) {
                classSessions.add(session.trim());
            }
            return classSessions;
        }
        return new ArrayList<>();
    }

    @Named("studentClassSessionsToIds")
    default List<String> studentClassSessionsToIds(List<StudentClassSession> sessions) {
        if (sessions == null) {
            return new ArrayList<>();
        }
        return sessions.stream()
                .map(StudentClassSession::getIdClassSession)
                .toList();
    }
}
