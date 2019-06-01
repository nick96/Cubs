package me.nspain.cubtracking.repository

import me.nspain.cubtracking.schemas.Cub
import org.apache.solr.client.solrj.impl.HttpSolrClient

class CubSolrRepository(val client: HttpSolrClient?): DocumentRepository<Cub, Cub.Update> {
    /** Get all available Cub documents. */
    override fun get(): List<Cub> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** Get the Cub document at [id]. */
    override fun get(id: Long): Cub? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** Get all Cub documents that fulfill [predicate]. */
    override fun filter(predicate: (Cub) -> Boolean): List<Cub> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** Update the Cub document at [id] with [update]. */
    override fun update(id: Long, update: Cub.Update.() -> Unit): Cub? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** Replace the Cub document at [id] with [replacement]. */
    override fun replace(id: Long, replacement: Cub): Cub? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** Delete Cub document with id [id]. */
    override fun delete(id: Long): Cub? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** Delete all the Cub documents that fulfill [predicate]. */
    override fun delete(predicate: (Cub) -> Boolean): List<Cub>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** Append [elem] to the collection of Cub documents. */
    override fun append(elem: Cub): Cub {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
