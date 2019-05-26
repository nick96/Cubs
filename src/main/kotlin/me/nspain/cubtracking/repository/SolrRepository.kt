package me.nspain.cubtracking.repository

import me.nspain.cubtracking.schemas.Cub
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.HttpSolrClient

class SolrRepository(private val solrAddr: String) : Repository {
    val client = HttpSolrClient.Builder(solrAddr).build()

    override fun getCubById(id: Long): Cub {
        TODO("not implemented")
    }

    override fun getCubs(): Collection<Cub> {
        TODO("not implemented")
    }


}