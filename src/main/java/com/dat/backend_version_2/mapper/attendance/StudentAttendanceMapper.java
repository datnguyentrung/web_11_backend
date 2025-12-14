package com.dat.backend_version_2.mapper.attendance;

import com.dat.backend_version_2.domain.attendance.StudentAttendance;
import com.dat.backend_version_2.dto.attendance.AttendanceDTO;
import com.dat.backend_version_2.dto.attendance.StudentAttendanceDTO;
import com.dat.backend_version_2.enums.attendance.AttendanceStatus;
import com.dat.backend_version_2.mapper.training.StudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class StudentAttendanceMapper {
    private final StudentMapper studentMapper;

    public static AttendanceDTO.StudentAttendanceKey studentAttendanceToAttendanceKey(StudentAttendance studentAttendance) {
        if (studentAttendance == null) {
            return null;
        }
        AttendanceDTO.StudentAttendanceKey attendanceKey = new AttendanceDTO.StudentAttendanceKey();
        attendanceKey.setIdUser(studentAttendance.getIdUser());
        attendanceKey.setIdClassSession(studentAttendance.getIdClassSession());
        attendanceKey.setAttendanceDate(studentAttendance.getAttendanceDate());
        return attendanceKey;
    }

    public static AttendanceDTO.AttendanceInfo studentAttendanceToAttendanceInfo(StudentAttendance studentAttendance) {
        if (studentAttendance == null) {
            return null;
        }
        AttendanceDTO.AttendanceInfo attendanceInfo = new AttendanceDTO.AttendanceInfo();
        attendanceInfo.setAttendance(studentAttendanceToAttendanceDetail(studentAttendance));
        attendanceInfo.setEvaluation(studentAttendanceToEvaluationDetail(studentAttendance));
        attendanceInfo.setNotes(studentAttendance.getNotes());
        return attendanceInfo;
    }

    public static AttendanceDTO.AttendanceDetail studentAttendanceToAttendanceDetail(StudentAttendance studentAttendance) {
        if (studentAttendance == null) {
            return null;
        }
        AttendanceDTO.AttendanceDetail attendanceDetail = new AttendanceDTO.AttendanceDetail();
        attendanceDetail.setAttendanceTime(
                studentAttendance.getAttendanceTime() != null ?
                        studentAttendance.getAttendanceTime() :
                        null
        );
        attendanceDetail.setAttendanceStatus(studentAttendance.getAttendanceStatus());
        if (studentAttendance.getAttendanceStatus() == AttendanceStatus.V ||
                studentAttendance.getAttendanceStatus() == AttendanceStatus.P) {
            return attendanceDetail;
        }
        attendanceDetail.setCoach(
                new AttendanceDTO.CoachReference(
                        studentAttendance.getAttendanceCoach().getIdAccount(),
                        studentAttendance.getAttendanceCoach().getName()
                )
        );
        return attendanceDetail;
    }

    public static AttendanceDTO.EvaluationDetail studentAttendanceToEvaluationDetail(StudentAttendance studentAttendance) {
        if (studentAttendance == null) {
            return null;
        }
        AttendanceDTO.EvaluationDetail evaluationDetail = new AttendanceDTO.EvaluationDetail();
        evaluationDetail.setEvaluationStatus(studentAttendance.getEvaluationStatus());
        if (studentAttendance.getEvaluationCoach() == null) {
            return evaluationDetail;
        }

        evaluationDetail.setCoach(
                new AttendanceDTO.CoachReference(
                        studentAttendance.getEvaluationCoach().getIdAccount(),
                        studentAttendance.getEvaluationCoach().getName()
                )
        );
        return evaluationDetail;
    }

    public StudentAttendanceDTO.StudentAttendanceDetail studentAttendanceToStudentAttendanceDetail(StudentAttendance s) {
        if (s == null) return null;

        AttendanceDTO.AttendanceInfo attendanceInfo = studentAttendanceToAttendanceInfo(s);
        var dto = new StudentAttendanceDTO.StudentAttendanceDetail();
        BeanUtils.copyProperties(attendanceInfo, dto);
        dto.setAttendanceDate(s.getAttendanceDate());
        dto.setPersonalAcademicInfo(studentMapper.studentToPersonalAcademicInfo(s.getStudent()));
        dto.setIdAccount(s.getStudent().getIdAccount());
        dto.setIdClassSession(s.getIdClassSession());
        return dto;
    }

    public static StudentAttendance attendanceInfoToStudentAttendance(AttendanceDTO.AttendanceInfo attendanceInfo) {
        if (attendanceInfo == null) {
            return null;
        }
        StudentAttendance studentAttendance = new StudentAttendance();
        studentAttendance.setAttendanceTime(LocalTime.now());
        studentAttendance.setAttendanceStatus(attendanceInfo.getAttendance().getAttendanceStatus());
        studentAttendance.setEvaluationStatus(attendanceInfo.getEvaluation().getEvaluationStatus());
        studentAttendance.setNotes(attendanceInfo.getNotes());
        return studentAttendance;
    }
}
