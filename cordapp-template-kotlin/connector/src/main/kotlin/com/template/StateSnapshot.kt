package com.template

import com.template.states.PriceState

object StateSnapshot {

    val priceMap = HashMap<String, Int>()

    fun add(priceState: PriceState) {
        priceMap.put(priceState.itemId, priceState.price)
    }

    fun getPrice(id: String): Int? {
        return priceMap.get(id)
    }
}