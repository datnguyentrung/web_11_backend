package com.dat.backend_version_2.service.training;

import com.dat.backend_version_2.domain.training.Branch;
import com.dat.backend_version_2.domain.training.ClassSession;
import com.dat.backend_version_2.domain.training.Student;
import com.dat.backend_version_2.dto.training.Student.StudentReq;
import com.dat.backend_version_2.dto.training.Student.StudentRes;
import com.dat.backend_version_2.enums.training.BeltLevel;
import com.dat.backend_version_2.mapper.training.StudentMapper;
import com.dat.backend_version_2.repository.training.StudentRepository;
import com.dat.backend_version_2.service.authentication.UsersService;
import com.dat.backend_version_2.util.ConverterUtils;
import com.dat.backend_version_2.util.error.IdInvalidException;
import com.dat.backend_version_2.util.error.UserNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final BranchService branchService;
    private final UsersService usersService;
    private final ClassSessionService classSessionService;
    private final StudentClassSessionService studentClassSessionService;

    /**
     * Asserts that a student exists and is active by account ID.
     *
     * @param idAccount the account ID to check.
     * @throws IllegalArgumentException  if idAccount is null or empty.
     * @throws ResourceNotFoundException if student does not exist or is inactive.
     */
    public void assertStudentIsActive(String idAccount) {
        // 1. Kiểm tra tham số đầu vào (Input Validation)
        if (idAccount == null || idAccount.trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }

        // 2. Gọi Repository để kiểm tra
        boolean existsAndActive = studentRepository.existsByIdAccountAndIsActive(idAccount, true);

        // 3. Assertion (Kiểm tra điều kiện)
        if (!existsAndActive) {
            // Ném ra Exception phù hợp khi điều kiện nghiệp vụ không được thỏa mãn
            log.warn("Attempted to access data for inactive/non-existent student with ID: {}", idAccount);
            throw new ResourceNotFoundException("Student with ID " + idAccount + " not found or is inactive.");
        }

        // Nếu check thành công, hàm kết thúc bình thường (void)
    }

    public Student createStudent(StudentReq.StudentInfo studentInfo) throws IdInvalidException, JsonProcessingException {
        Student student = new Student();

        // Academic Info
        Branch branch = branchService.getBranchById(studentInfo.getAcademic().getIdBranch());
        student.setBranch(branch);
        student.setBeltLevel(
                Optional.ofNullable(studentInfo.getAcademic().getBeltLevel())
                        .orElse(BeltLevel.C10)
        );
        student.setIsActive(
                Optional.ofNullable(studentInfo.getAcademic().getIsActive())
                        .orElse(true)
        );

        // Personal Info
        studentMapper.personalInfoToStudent(studentInfo.getPersonal(), student);
        // Contact Info
        studentMapper.contactInfoToStudent(studentInfo.getContact(), student);
        // Enrollment Info
        studentMapper.enrollmentInfoToStudent(studentInfo.getEnrollment(), student);

        // Gọi hàm setup chung cho Users
        usersService.setupBaseUser(student, "STUDENT");

        // Lưu student TRƯỚC để generate ID
        student = studentRepository.save(student);

        // SAU ĐÓ mới tạo relationships với ClassSession
        studentClassSessionService.createStudentClassSession(
                student,
                studentInfo.getAcademic().getClassSession()
        );

        return student;
    }

    public List<StudentRes.PersonalAcademicInfo> getAllStudents() {
        return studentRepository.findAllWithBranchAndSessions().stream()
                .map(studentMapper::studentToPersonalAcademicInfo) // tái sử dụng hàm đã viết
                .toList();
    }

    public Student getStudentById(String idUser) {
        return studentRepository.findById(UUID.fromString(idUser))
                .orElseThrow(() -> new UserNotFoundException(
                        "Không tìm thấy học viên với id: " + idUser
                ));
    }

    public List<StudentRes.PersonalAcademicInfo> getStudentsByIdBranch(Integer idBranch) throws IdInvalidException, JsonProcessingException {
        Branch branch = branchService.getBranchById(idBranch);
        return studentRepository.findByBranch(branch).stream()
                .map(studentMapper::studentToPersonalAcademicInfo)
                .toList();
    }

    public List<StudentRes.PersonalAcademicInfo> getStudentsByIdClassSession(String idClassSession) throws IdInvalidException, JsonProcessingException {
        ClassSession classSession = classSessionService.getClassSessionById(idClassSession);

        if (!classSession.getIsActive()) {
            throw new RuntimeException("ClassSession is not active");
        }

        return studentRepository.findAllByIdClassSession(classSession).stream()
                .map(studentMapper::studentToPersonalAcademicInfo)
                .toList();
    }

    public Student getStudentByIdAccount(String idAccount) {
        return studentRepository.findByIdAccount(idAccount)
                .orElseThrow(() -> new UserNotFoundException("Student not found with id: " + idAccount));
    }

    /**
     * Gets an active student by account ID
     *
     * @param idAccount the account ID to search for
     * @return the active student
     * @throws IllegalArgumentException if idAccount is null or empty
     * @throws UserNotFoundException    if no active student found with the given account ID
     */
    public Student getActiveStudentByIdAccount(String idAccount) throws IllegalArgumentException, UserNotFoundException {
        if (idAccount == null || idAccount.trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }

        return studentRepository.findByIdAccountAndIsActive(idAccount, true)
                .orElseThrow(() -> new UserNotFoundException(
                        "No active student found with account ID: " + idAccount
                ));
    }

    public List<StudentRes.PersonalAcademicInfo> searchStudents(String keyword) {
        // 1. Kiểm tra null và xóa khoảng trắng thừa ở đầu/cuối
        if (keyword == null) {
            return List.of();
        }

        String normalizedKeyword = ConverterUtils.removeAccents(keyword.trim());

        // Nếu sau khi xóa khoảng trắng mà chuỗi rỗng -> Trả về list rỗng
        if (normalizedKeyword.isEmpty()) {
            return List.of();
        }

        // 2. Kiểm tra xem có phải tìm kiếm theo số điện thoại không
        // Regex này kiểm tra: Chuỗi chỉ chứa số, dấu +, dấu -, dấu ngoặc (), hoặc khoảng trắng
        // Ví dụ khớp: "090", "+84", "098-123", "(024)"
        boolean isPhoneSearch = normalizedKeyword.matches("^[0-9+\\-\\(\\) ]+$");

        if (isPhoneSearch) {
            // LOGIC TÌM THEO SỐ ĐIỆN THOẠI
            String searchPhone = normalizedKeyword.replaceAll("[^0-9]", ""); // Chỉ giữ lại chữ số
            return studentRepository.searchByPhoneNumber(searchPhone).stream()
                    .map(studentMapper::studentQuickViewToPersonalAcademicInfo)
                    .toList();
        } else {
            // LOGIC TÌM THEO TÊN
            String searchName = normalizedKeyword.toLowerCase();
            return studentRepository.searchByName(searchName).stream()
                    .map(studentMapper::studentQuickViewToPersonalAcademicInfo)
                    .toList();
//            throw new UnsupportedOperationException("Search by name is not implemented yet.");
        }
    }
}

