package com.example.demo.dto;

import com.example.demo.entity.Pet;
import com.example.demo.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InitialPetResponseDto {
    private Pet pet;
    private Room room;
}