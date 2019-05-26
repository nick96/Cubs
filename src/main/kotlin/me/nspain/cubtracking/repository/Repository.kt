package me.nspain.cubtracking.repository

import me.nspain.cubtracking.schemas.Cub

interface Repository {
    fun getCubById(id: Long): Cub
    fun getCubs(): Collection<Cub>
}