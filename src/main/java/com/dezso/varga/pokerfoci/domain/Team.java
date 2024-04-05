package com.dezso.varga.pokerfoci.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "TEAM_ACCOUNT",
            joinColumns = @JoinColumn(name = "TEAM_ID"),
            inverseJoinColumns = @JoinColumn(name = "ACCOUNT_ID")
    )
    private List<TeamMember> teamMembers;

    private int skillSum;

    public Team() {
    }

    public Team(List<TeamMember> teamMembers, int skillSum) {
        this.teamMembers = teamMembers;
        this.skillSum = skillSum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<TeamMember> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<TeamMember> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public int getSkillSum() {
        return skillSum;
    }

    public void setSkillSum(int skillSum) {
        this.skillSum = skillSum;
    }
}
