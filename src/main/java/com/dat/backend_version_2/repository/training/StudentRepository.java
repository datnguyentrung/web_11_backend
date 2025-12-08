package com.dat.backend_version_2.repository.training;

import com.dat.backend_version_2.domain.training.Branch;
import com.dat.backend_version_2.domain.training.ClassSession;
import com.dat.backend_version_2.domain.training.Student;
import com.dat.backend_version_2.dto.training.Student.StudentRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

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
            SELECT s.id_user                    AS idUser,
                u.id_account                    AS idAccount,
                s.name                          AS name,
                s.id_national                   AS idNational,
                s.birth_date                    AS birthDate,
                s.is_active                     AS isActive,
                s.branch                        AS idBranch,
                s.belt_level                    AS beltLevel,
            
                (SELECT COALESCE(STRING_AGG(scs.id_class_session, ','), '')
                 FROM training.student_class_session scs
                 WHERE scs.id_user = s.id_user) AS classSessions
            FROM training.student s
                 LEFT JOIN authentication.users u on s.id_user = u.id_user
            WHERE s.phone LIKE '%' || :phone || '%'
              AND s.is_active = TRUE
            GROUP BY s.id_user, u.id_account
            LIMIT 20""", nativeQuery = true)
    List<StudentQuickView> searchByPhoneNumber(
            @Param("phone") String searchPhone
    );

    @Query(value = """
            SELECT s.id_user                    AS idUser,
                u.id_account                    AS idAccount,
                s.name                          AS name,
                s.id_national                   AS idNational,
                s.birth_date                    AS birthDate,
                s.is_active                     AS isActive,
                s.branch                        AS idBranch,
                s.belt_level                    AS beltLevel,
            
                (SELECT COALESCE(STRING_AGG(scs.id_class_session, ','), '')
                 FROM training.student_class_session scs
                 WHERE scs.id_user = s.id_user) AS classSessions
            FROM training.student s
                 LEFT JOIN authentication.users u on s.id_user = u.id_user
            WHERE public.f_unaccent(s.name) ILIKE '%' || public.f_unaccent(:keyword) || '%'
              AND s.is_active = TRUE
            LIMIT 20""", nativeQuery = true)
    List<StudentQuickView> searchByName(
            @Param("keyword") String searchName
    );

    List<Student> findAllByIdAccountIn(Collection<String> idAccounts);
}
