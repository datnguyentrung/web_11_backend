package com.dat.backend_version_2.mapper.training;

import com.dat.backend_version_2.domain.training.Student;
import com.dat.backend_version_2.domain.training.StudentClassSession;
import com.dat.backend_version_2.dto.authentication.UserRes;
import com.dat.backend_version_2.dto.training.Student.StudentReq;
import com.dat.backend_version_2.dto.training.Student.StudentRes;
import com.dat.backend_version_2.enums.training.BeltLevel;
import com.dat.backend_version_2.repository.training.StudentQuickView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StudentMapper {
    public static void personalInfoToStudent(StudentReq.PersonalInfo personalInfo, Student student) {
        if (personalInfo == null) {
            return;
        }
        student.setName(personalInfo.getName());
        String idUserStr = personalInfo.getIdUser();
        if (idUserStr != null && !idUserStr.isBlank()) {
            try {
                UUID parsedId = UUID.fromString(idUserStr);
                student.setIdUser(parsedId);
            } catch (IllegalArgumentException e) {
                // Nếu chuỗi không phải UUID hợp lệ thì bạn có thể:
                // - bỏ qua để giữ UUID mặc định
                // - hoặc throw exception để báo input sai
                // Ở đây mình giả sử giữ mặc định
            }
        }
        student.setIdNational(personalInfo.getIdNational());
        student.setBirthDate(personalInfo.getBirthDate());
    }

    public static void contactInfoToStudent(StudentReq.ContactInfo contactInfo, Student student) {
        if (contactInfo == null) {
            return;
        }
        student.setPhone(contactInfo.getPhone());
        student.setMember(contactInfo.getMember());
    }

    public static void enrollmentInfoToStudent(StudentReq.EnrollmentInfo enrollmentInfo, Student student) {
        if (enrollmentInfo == null) {
            return;
        }
        student.setStartDate(
                Optional.ofNullable(enrollmentInfo.getStartDate())
                        .orElse(LocalDate.now())
        );
        student.setEndDate(enrollmentInfo.getEndDate());
    }

    public static StudentRes.PersonalInfo studentToPersonalInfo(Student student) {
        if (student == null) return null;
        return new StudentRes.PersonalInfo(
                student.getName(),
                student.getIdAccount(),
                student.getIdNational(),
                student.getBirthDate(),
                student.getIsActive()
        );
    }

    public static StudentRes.AcademicInfo studentToAcademicInfo(Student student) {
        if (student == null) return null;
        return new StudentRes.AcademicInfo(
                student.getBranch().getIdBranch(),
                student.getBeltLevel(),
                student.getStudentClassSessions()
                        .stream()
                        .map(StudentClassSession::getIdClassSession)
                        .toList()
        );
    }

    public static StudentRes.PersonalAcademicInfo studentToPersonalAcademicInfo(Student student) {
        if (student == null) return null;

        StudentRes.PersonalInfo personalInfo = studentToPersonalInfo(student);
        StudentRes.AcademicInfo academicInfo = studentToAcademicInfo(student);

        return new StudentRes.PersonalAcademicInfo(personalInfo, academicInfo);
    }

    public static UserRes.UserInfo studentToUserInfo(Student student) {
        if (student == null) return null;
        return new UserRes.UserInfo(
                student.getIdUser(),
                student.getIdAccount(),
                student.getEmail(),
                student.getRole().getIdRole()
        );
    }

    public static UserRes.UserProfile studentToUserProfile(Student student) {
        if (student == null) return null;
        return new UserRes.UserProfile(
                student.getBirthDate(),
                student.getIsActive(),
                student.getName(),
                student.getPhone(),
                student.getBeltLevel()
        );
    }

    public static UserRes studentToUserRes(Student student) {
        if (student == null) return null;
        return new UserRes(
                studentToUserInfo(student),
                studentToUserProfile(student)
        );
    }

    public static StudentRes.PersonalInfo studentQuickViewToPersonalInfo(StudentQuickView view) {
        if (view == null) return null;
        LocalDate birthDate = null;
        if (view.getBirthDate() != null && !view.getBirthDate().isBlank()) {
            birthDate = LocalDate.parse(view.getBirthDate());
        }
        return new StudentRes.PersonalInfo(
                view.getName(),
                view.getIdAccount(),
                view.getIdNational(),
                birthDate,
                view.getIsActive()
        );
    }

    public static StudentRes.AcademicInfo studentQuickViewToAcademicInfo(StudentQuickView view) {
        if (view == null) return null;
        BeltLevel beltLevel = null;
        if (view.getBeltLevel() != null && !view.getBeltLevel().isBlank()) {
            beltLevel = BeltLevel.valueOf(view.getBeltLevel());
        }
        // Xử lý chuỗi lớp học thành danh sách
        List<String> classSessions = new ArrayList<>();
        if (view.getClassSessions() != null && !view.getClassSessions().isBlank()) {
            String[] sessionsArray = view.getClassSessions().split(",");
            for (String session : sessionsArray) {
                classSessions.add(session.trim());
            }
        }
        return new StudentRes.AcademicInfo(
                view.getIdBranch(),
                beltLevel,
                classSessions
        );
    }

    public static StudentRes.PersonalAcademicInfo studentQuickViewToPersonalAcademicInfo (StudentQuickView view){
        if (view == null) return null;

        StudentRes.PersonalInfo personalInfo = studentQuickViewToPersonalInfo(view);
        StudentRes.AcademicInfo academicInfo = studentQuickViewToAcademicInfo(view);

        return new StudentRes.PersonalAcademicInfo(personalInfo, academicInfo);
    }
}
