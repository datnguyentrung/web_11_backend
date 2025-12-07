package com.dat.backend_version_2.repository.training;

import com.dat.backend_version_2.domain.training.Branch;
import com.dat.backend_version_2.domain.training.ClassSession;
import com.dat.backend_version_2.domain.training.Student;
import com.dat.backend_version_2.dto.training.Student.StudentRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    @Query("""
            SELECT DISTINCT s FROM Student s
                        JOIN FETCH s.branch b
                        LEFT JOIN FETCH s.studentClassSessions scs
                        LEFT JOIN FETCH scs.classSession cs
            """)
    List<Student> findAllWithBranchAndSessions();

    List<Student> findByBranch(Branch branch);

    @Query("""
            SELECT DISTINCT s FROM Student s
                        JOIN FETCH s.studentClassSessions scs
            WHERE scs.classSession = :classSession
            """)
    List<Student> findAllByIdClassSession(@Param("classSession") ClassSession classSession);

    Optional<Student> findByIdAccount(String idAccount);

    /**
     * Checks if a student exists and is active by account ID
     *
     * @param idAccount the account ID to check
     * @param isActive  the active status to check
     * @return true if a student with the given account ID and active status exists, false otherwise
     */
    boolean existsByIdAccountAndIsActive(String idAccount, Boolean isActive);

    Optional<Student> findByIdAccountAndIsActive(String idAccount, Boolean isActive);

    @Query(value = """
            SELECT *
            FROM training.student s
            WHERE s.phone LIKE '%' || :phone || '%'""", nativeQuery = true)
    List<StudentRes.PersonalAcademicInfo> searchByPhoneNumber(
            @Param("phone") String searchPhone
    );

    List<StudentRes.PersonalAcademicInfo> searchByName(String searchName);
}
