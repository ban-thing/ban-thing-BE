package com.example.banthing.domain.item.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cleaning_details")
public class CleaningDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cln_pollution")
    private String pollution;

    @Column(name = "cln_time_used")
    private String timeUsed;

    @Column(name = "cln_purchased_date")
    private String purchasedDate;

    @Column(name = "cln_cleaned")
    private String cleaned;

    @Column(name = "cln_expire")
    private String expire;

}
