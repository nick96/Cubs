package me.nspain.cubtracking.repository

interface DocumentRepository<D, U> {
    /** Get all available Cub documents. */
    fun get(): List<D>

    /** Get the Cub document at [id]. */
    operator fun get(id: Long): D?

    /** Get all Cub documents that fulfill [predicate]. */
    fun filter(predicate: (D) -> Boolean): List<D>

    /** Update the Cub document at [id] with [update]. */
    fun update(id: Long, update: U.() -> Unit): D?

    /** Replace the Cub document at [id] with [replacement]. */
    fun replace(id: Long, replacement: D): D?

    /** Delete Cub document with id [id]. */
    fun delete(id: Long): D?

    /** Delete all the Cub documents that fulfill [predicate]. */
    fun delete(predicate: (D) -> Boolean): List<D>?

    /** Append [elem] to the collection of Cub documents. */
    fun append(elem: D): D
}