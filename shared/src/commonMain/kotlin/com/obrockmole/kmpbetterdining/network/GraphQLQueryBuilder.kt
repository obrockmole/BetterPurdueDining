package com.obrockmole.betterdining.network

fun buildMultiItemQuery(itemIds: List<String>): String {
    val aliases = itemIds.mapIndexed { i, itemId ->
        """
          item$i: itemByItemId(itemId: "$itemId") {
            name
            itemId
            appearances {
              locationName
              mealName
              date
            }
          }"""
    }

    return """
        query GetMultipleItems {
        ${aliases.joinToString("\n")}
        }
        """
}
