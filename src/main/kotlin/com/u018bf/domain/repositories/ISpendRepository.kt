package com.u018bf.domain.repositories

import com.u018bf.domain.models.Spend


interface ISpendRepository {
    fun save(spends: List<Spend>)
}