package com.knuissant.dailyq.domain.jobs;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "occupations")
public class Occupation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "occupation_id")
    private Long id;

    @Column(name = "occupation_name", nullable = false, unique = true, length = 100)
    private String name;

    @OneToMany(mappedBy = "occupation", fetch = FetchType.LAZY)
    private List<Job> jobs = new ArrayList<>();
}

