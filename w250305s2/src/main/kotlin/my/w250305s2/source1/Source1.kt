package my.w250305s2.source1

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Table(name = "tw__towar")
data class MyEntity1(
    @Id
    @Column("tw_id")
    val id: Int,

    @Column("tw_symbol")
    val symbol: String?,
)

//@Transactional
@Repository
@Qualifier("dataSource1")
interface Source1Repo : CrudRepository<MyEntity1, Int>
