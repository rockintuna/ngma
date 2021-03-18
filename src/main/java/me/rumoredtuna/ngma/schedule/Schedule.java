package me.rumoredtuna.ngma.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.rumoredtuna.ngma.account.Account;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Schedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm")
    private LocalDateTime dateTime = LocalDateTime.now();

    private String title;

    private String place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private Account owner;

    private boolean personal;

    public Schedule(ScheduleDto scheduleDto) {
        this.dateTime = scheduleDto.getDateTime();
        this.title = scheduleDto.getTitle();
        this.place = scheduleDto.getPlace();
        this.personal = scheduleDto.isPersonal();
    }

    public void modifyByDto(ScheduleDto scheduleDto) {
        this.dateTime = scheduleDto.getDateTime();
        this.title = scheduleDto.getTitle();
        this.place = scheduleDto.getPlace();
        this.personal = scheduleDto.isPersonal();
    }

}
