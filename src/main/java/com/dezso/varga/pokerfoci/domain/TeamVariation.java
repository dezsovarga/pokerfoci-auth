package com.dezso.varga.pokerfoci.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Builder
@AllArgsConstructor
public class TeamVariation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    private Team team1;

    @OneToOne
    private Team team2;

    private double skillDifference;

    private boolean selectedForVoting;

    public TeamVariation() {

    }

    public TeamVariation(Team team1, Team team2, double skillDifference, boolean selectedForVoting) {
        this.team1 = team1;
        this.team2 = team2;
        this.skillDifference = skillDifference;
        this.selectedForVoting = selectedForVoting;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getTeam1() {
        return team1;
    }

    public void setTeam1(Team team1) {
        this.team1 = team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public void setTeam2(Team team2) {
        this.team2 = team2;
    }

    public double getSkillDifference() {
        return skillDifference;
    }

    public void setSkillDifference(double skillDifference) {
        this.skillDifference = skillDifference;
    }

    public boolean isSelectedForVoting() {
        return selectedForVoting;
    }

    public void setSelectedForVoting(boolean selectedForVoting) {
        this.selectedForVoting = selectedForVoting;
    }

}
