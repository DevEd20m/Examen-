package com.faztbit.examen.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Medal(
    val id: Int,
    val name: String,
    val currentLevel: Int,
    val currentPoints: Int,
    val maxLevel: Int,
    val iconResource: String
) {
    val isMaxLevel: Boolean
        get() = currentLevel >= maxLevel
    
    val progressPercentage: Float
        get() = if (isMaxLevel) 1.0f else (currentPoints % 100) / 100.0f
    
    fun levelUp(): Medal {
        return if (!isMaxLevel && currentPoints >= 100) {
            copy(
                currentLevel = currentLevel + 1,
                currentPoints = currentPoints - 100
            )
        } else {
            this
        }
    }
    
    fun addPoints(points: Int): Medal {
        return if (!isMaxLevel) {
            copy(currentPoints = currentPoints + points)
        } else {
            this
        }
    }
}