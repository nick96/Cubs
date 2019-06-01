package me.nspain.cubtracking.repository

import me.nspain.cubtracking.schemas.AchievementBadge
import me.nspain.cubtracking.schemas.Cub
import me.nspain.cubtracking.schemas.SpecialInterestBadge

interface Repository {
    /** Connection string for the repository. **/
    val connectionString: String

    /** Repository for accessing Cub documents. **/
    val cubRepository: DocumentRepository<Cub, Cub.Update>
}