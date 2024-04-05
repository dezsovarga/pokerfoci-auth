package com.dezso.varga.pokerfoci.services

import com.dezso.varga.pokerfoci.domain.Account
import com.dezso.varga.pokerfoci.domain.Team
import com.dezso.varga.pokerfoci.domain.TeamMember
import com.dezso.varga.pokerfoci.domain.TeamVariation
import kotlin.math.roundToInt
import org.springframework.stereotype.Service
import org.paukov.combinatorics3.Generator

@Service
class TeamGeneratorService {

    fun generateVariations(players: List<Account>): List<TeamVariation>? {
        val teamVariations = Generator.combination(players)
            .simple(players.size/2)
            .map { accounts ->
                accounts.sortByDescending { player -> player.skill }
                val teamMembers = accounts.map { TeamMember(it) }
                val team1 = Team(teamMembers, accounts.sumOf { it.skill })
                val team2 = getOtherTeam(team1, players)
                TeamVariation(team1, team2, getTeamDifference(team1, team2))
            }.filter { variation -> variation.skillDifference < 3 }.sortedBy { it.skillDifference }
        return teamVariations.take(20).filterIndexed { index, _ -> index % 2 == 0  }
    }

    private fun getOtherTeam(team1: Team, allPlayers: List<Account>): Team {
        val team2Players = allPlayers.filter { player -> !team1.teamMembers.any { it.account == player } }.sortedByDescending { it.skill }
        val team2Members = team2Players.map { TeamMember(it) }
        return Team(team2Members, team2Players.sumOf { it.skill })
    }

    private fun getTeamDifference(team1: Team, team2: Team) : Double {
        val diff = kotlin.math.abs(team1.teamMembers.sumOf { it.account.skill } - team2.teamMembers.sumOf { it.account.skill })
        return (diff * 100.0).roundToInt() / 100.0
    }
}