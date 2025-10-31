package com.dezso.varga.pokerfoci.domain

import javax.persistence.*

@Entity
data class Vote(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    val id: Long,

    @OneToOne
    @JoinColumn(name = "participation_id")
    val participation: Participation,

    @OneToOne
    @JoinColumn(name = "team_variation_id")
    val teamVariation: TeamVariation
)