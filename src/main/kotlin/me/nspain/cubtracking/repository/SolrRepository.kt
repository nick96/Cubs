package me.nspain.cubtracking.repository

import me.nspain.cubtracking.schemas.Cub
import org.apache.solr.client.solrj.impl.HttpSolrClient

class SolrRepository(override val connectionString: String) : Repository {
    val client = HttpSolrClient.Builder(connectionString).build()

    override fun updateCubByID(id: Long): Cub {
        TODO("not implemented")
    }

    override fun addCub(cub: Cub): Cub {
        TODO("not implemented")
    }

    override fun deleteCub(id: Long): Cub {
        TODO("not implemented")
    }

    override fun getCubById(id: Long): Cub {
        TODO("not implemented")
    }

    override fun getCubs(): Collection<Cub> {
        TODO("not implemented")
    }


}