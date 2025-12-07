package io.mipangg.holidaykeeper.domain.holiday.entity;

import io.mipangg.holidaykeeper.domain.common.BaseEntity;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.holiday.dto.ExternalHolidayResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;

@Entity
@Builder
@Getter
@SoftDelete
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Holiday extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String localName;

    @Column(nullable = false)
    private String name;

    private boolean isFixed;

    private boolean isGlobal;

    private Integer launchYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    public void update(ExternalHolidayResponse externalHoliday) {
        this.date = LocalDate.parse(externalHoliday.date());
        this.localName = externalHoliday.localName();
        this.name = externalHoliday.name();
        this.isFixed = externalHoliday.fixed();
        this.isGlobal = externalHoliday.global();
        this.launchYear = externalHoliday.launchYear();
    }

}
