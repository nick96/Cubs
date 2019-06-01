// Resources returned by the API
package me.nspain.cubtracking.schemas

import java.io.Serializable
import java.util.*

// Cub completing the various achievements.
class Cub(
        val id: Long,
        // Cub's name.
        var name: String,
        // Collection of achievement badges the cub has attained.
        var achievementBadges: List<AchievementBadge> = listOf(),
        // Collection of special interest badges the cub has attained.
        var specialInterestBadges: Collection<SpecialInterestBadge> = listOf(),
        // Cub's bronze boomerang progress.
        var bronzeBoomerang: Boomerang? = null,
        // Cub's silver boomerang progress.
        var silverBoomerang: Boomerang? = null,
        // Cub's gold boomerang progress.
        var goldBoomerang: Boomerang? = null
): Serializable {
    class Update {
        var name: String? = null
        var achievementBadges: List<AchievementBadge>? = null
        var specialInterestBadges: List<SpecialInterestBadge>? = null
        var bronzeBoomerang: Boomerang? = null
        var silverBoomerang: Boomerang? = null
        var goldBoomerang: Boomerang? = null
    }
}

// Achievement badge.
data class AchievementBadge(
        val id: Long,
        // Name of the achievement badge.
        val name: String,
        // Level the achievement badge is for.
        val level: Int,
        // Tasks completed or in the process of being completed for this badge.
        val tasks: Collection<Task>,
        // Number of tasks required to be completed.
        val numRequiredTasks: Int?,
        // Date the requirement was completed.
        val completionDate: Date?,
        // Who signed of the completion of this requirement.
        val signed: String?
) : Serializable

// Special interest badge.
data class SpecialInterestBadge(
        val id: Long,
        // Name of the special interest badge.
        val name: String,
        // Requirements that have been completed or are in the process of being completed for this badge.
        val requirements: Collection<Requirement>,
        // Number of requirements required to be completed.
        val numRequirements: Int?,
        // Date the requirement was completed.
        val completionDate: Date?,
        // Who signed of the completion of this requirement.
        val signed: String?
) : Serializable

// Boomerang.
data class Boomerang(
        val id: Long,
        // Boomerang name.
        val name: String,
        // Tasks completed or in the process of being completed for this boomerang.
        val tasks: Collection<Task>,
        // Number of tasks required to be completed.
        val numRequiredTasks: Int?,
        // Date the requirement was completed.
        val completionDate: Date?,
        // Who signed of the completion of this requirement.
        val signed: String?
) : Serializable

// Task to be completed for an achievement.
data class Task(
        val id: Long,
        // Task name
        val name: String,
        // Completed requirements or requirements in the process of being completed.
        val requirements: Collection<Requirement>,
        // Number of requirements required to be completed.
        val numRequirements: Int?,
        // Date the requirement was completed.
        val completionDate: Date?,
        // Who signed of the completion of this requirement.
        val signed: String?
) : Serializable

// One part of the requirements that must be completed to fulfill a task.
data class Requirement(
        val id: Long,
        // Requirement's topic (e.g. 'Recycling').
        val topic: String,
        // Description of the requirement.
        // Whilst this is a bit confusing because a Requirement is part of a Task. We are trying to use the domain language
        // and the requirement description is referred to task the task.
        val task: String,
        // Date the requirement was completed.
        val completionDate: Date?,
        // Who signed of the completion of this requirement.
        val signed: String?
) : Serializable

// Record an outing or activity
data class OutingRecord(
        val id: Long,
        val name: String,
        val description: String,
        val date: Date
) : Serializable