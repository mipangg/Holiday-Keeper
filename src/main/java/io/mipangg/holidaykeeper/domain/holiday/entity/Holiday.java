package io.mipangg.holidaykeeper.domain.holiday.entity;

import io.mipangg.holidaykeeper.domain.common.BaseEntity;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
        name = "holiday",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_holiday_date_local_name_country_id", // 제약조건 이름
                        columnNames = {"date", "local_name", "country_id"} // 유니크 제약 걸 컬럼들
                )
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE holiday SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Holiday extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String localName;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean fixed;

    @Column(nullable = false)
    private boolean global;

    private Integer launchYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(nullable = false)
    private boolean deleted;

    public void update(String name, boolean fixed, boolean global, Integer launchYear) {
        this.name = name + "(updated)";
        this.fixed = fixed;
        this.global = global;
        this.launchYear = launchYear;
    }

}
