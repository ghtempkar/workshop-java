package my.w250305s2.source1

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Table(name = "tw__Towar")
data class MyEntity1(
    @Id
    @Column("tw_id")
    val id: Int,

    @Column("tw_symbol")
    val symbol: String?,
)

@Repository
@Qualifier("dataSource1")
interface Source1Repo : CrudRepository<MyEntity1, Int>
