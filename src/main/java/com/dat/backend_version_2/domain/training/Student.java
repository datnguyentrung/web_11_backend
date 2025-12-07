package com.dat.backend_version_2.domain.training;

import com.dat.backend_version_2.domain.authentication.Users;
import com.dat.backend_version_2.enums.training.Student.Member;
import com.dat.backend_version_2.enums.training.BeltLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "idUser") // chỉ định PK join với Users.idUser
@Table(
        name = "Student",
        schema = "training",
        indexes = {
                @Index(name = "idx_student_branch", columnList = "branch"),
                @Index(name = "idx_student_belt_level", columnList = "beltLevel"),
                @Index(name = "idx_student_is_active", columnList = "isActive")
        }
)
public class Student extends Users {
    private String idNational;

    @Column(nullable = false)
    private Boolean isActive = true;
    private String name;
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "branch", referencedColumnName = "idBranch")
    @JsonIgnore
    private Branch branch;

    @Enumerated(EnumType.STRING)
    private BeltLevel beltLevel = BeltLevel.C10;

    @OneToMany(mappedBy = "student", cascade = {CascadeType.REMOVE, CascadeType.MERGE}, orphanRemoval = true) // ✅ SỬA ĐỔI
    @JsonIgnore
    @EqualsAndHashCode.Exclude // 👈 THÊM DÒNG NÀY: Chặn sinh equals/hashCode
    @ToString.Exclude          // 👈 THÊM DÒNG NÀY: Chặn sinh toString (phòng hờ)
    private List<StudentClassSession> studentClassSessions = new ArrayList<>();

    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Member member;

    private String phone;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Student student = (Student) o;
        // Chỉ so sánh ID (giả sử getIdUser() lấy từ lớp cha Users)
        return getIdUser() != null && Objects.equals(getIdUser(), student.getIdUser());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
