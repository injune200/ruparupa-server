package com.example.roomlook.dto;

import com.example.roomlook.dto.PlazaPosition;
import lombok.Data;

@Data
public class PlazaMovementCommand {
    private PlazaPosition from;
    private PlazaPosition to;
    private Long startedAtMillis;
    private Long durationMillis;
}
//이동 명령