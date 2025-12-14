package com.dat.backend_version_2.domain.tournament.Poomsae;

import com.dat.backend_version_2.domain.achievement.PoomsaeList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "PoomsaeHistory", schema = "tournament")
public class PoomsaeHistory {
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(updatable = false, nullable = false)
    private UUID idPoomsaeHistory;

    @ManyToOne
    @JoinColumn(name = "poomsae_list")
    @JsonIgnore
    private PoomsaeList poomsaeList;

    private Integer sourceNode;       // node gốc
    private Integer targetNode;       // node mục tiêu
    private Integer levelNode;
    private Boolean hasWon;

    @CreationTimestamp // Tự động gán thời gian hiện tại khi bản ghi được lưu lần đầu
    @Column(name = "created_at", updatable = false) // Không cho phép cập nhật sau khi tạo
    private LocalDateTime createdAt;

    @UpdateTimestamp // Tự động cập nhật mỗi khi bản ghi được chỉnh sửa
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
