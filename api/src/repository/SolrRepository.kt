package me.nspain.cubtracking.repository

import me.nspain.cubtracking.schemas.Cub
import org.apache.solr.client.solrj.impl.HttpSolrClient

class SolrRepository(override val connectionString: String) : Repository {
    val client = HttpSolrClient.Builder(connectionString).build()
    override val cubRepository = CubSolrRepository(client)
}