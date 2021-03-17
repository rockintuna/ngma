package me.rumoredtuna.ngma.schedule;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ScheduleDto {

    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateTime = LocalDateTime.now();

    private String title;

    private String place;

    private boolean personal;

}
