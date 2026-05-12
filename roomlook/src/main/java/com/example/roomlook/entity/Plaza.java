package com.example.roomlook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Plaza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plazaId;
    private String plazaCode;

    private Integer roomRevision;

    private Long createdAtMillis;


    @OneToMany(mappedBy = "plaza", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PlazaParticipant> participants = new ArrayList<>();
}