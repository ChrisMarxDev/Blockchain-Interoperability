package com.template

import com.template.states.PriceState

object StateSnapshot {

    val priceMap = HashMap<String, Int>()

    fun add(priceState: PriceState) {
        add(priceState.itemId, priceState.price)
    }

    fun add(itemId: String, price: Int) {
        println("saved ${itemId} with price ${price}")
        priceMap.put(itemId, price)
    }

    fun getPrice(id: String): Int? {
        return priceMap.get(id)
    }
}