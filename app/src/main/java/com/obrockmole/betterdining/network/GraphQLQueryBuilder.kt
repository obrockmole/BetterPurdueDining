package com.obrockmole.betterdining.network

fun buildMultiItemQuery(itemIds: List<String>): String {
    val aliases = itemIds.mapIndexed { i, itemId ->
        """
          item$i: itemByItemId(itemId: "$itemId") {
            itemId
            name
            appearances {
              date
              locationName
              stationName
              mealName
            }
          }"""
    }

    return """
        query GetMultipleItems {
        ${aliases.joinToString("\n")}
        }
        """
}
