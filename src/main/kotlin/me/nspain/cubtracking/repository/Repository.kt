package me.nspain.cubtracking.repository

import me.nspain.cubtracking.schemas.Cub

interface Repository {
    val connectionString: String

    fun getCubById(id: Long): Cub
    fun getCubs(): Collection<Cub>
    fun updateCubByID(id: Long): Cub
    fun addCub(cub: Cub): Cub
    fun deleteCub(id: Long): Cub
}