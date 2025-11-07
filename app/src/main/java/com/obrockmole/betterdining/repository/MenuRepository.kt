package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.models.GraphQLRequest
import com.obrockmole.betterdining.models.ItemDetailsResponse
import com.obrockmole.betterdining.models.ItemVariables
import com.obrockmole.betterdining.models.MenuResponse
import com.obrockmole.betterdining.models.Variables
import com.obrockmole.betterdining.network.RetrofitInstance

class MenuRepository {
    private val query = """
        query getLocationMenu(${'$'}name: String!, ${'$'}date: Date!) {
          diningCourtByName(name: ${'$'}name) {
            name
            formalName
            id
            lineLength
            dailyMenu(date: ${'$'}date) {
              notes
              meals {
                name
                notes
                status
                startTime
                endTime
                stations {
                  name
                  id
                  items {
                    specialName
                    itemMenuId
                    hasComponents
                    item {
                      name
                      itemId
                      traits {
                        name
                        svgIcon
                        svgIconWithoutBackground
                      }
                      components {
                        name
                        itemId
                        traits {
                          name
                          svgIcon
                          svgIconWithoutBackground
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
    """.trimIndent()

    private val itemQuery = """
        query (${'$'}id: Guid!) {
          itemByItemId(itemId: ${'$'}id) {
            name
            itemId
            ingredients
            isNutritionReady
            nutritionFacts {
              name
              value
              label
              dailyValueLabel
            }
            traits {
              name
              type
              svgIcon
              svgIconWithoutBackground
            }
            appearances {
              mealName
              locationName
              stationName
              date
            }
            components {
              name
              itemId
              isFlaggedForCurrentUser
              isHiddenForCurrentUser
              isNutritionReady
              traits {
                name
                type
                svgIcon
                svgIconWithoutBackground
              }
            }
          }
        }
    """.trimIndent()

    suspend fun getDiningCourtMenu(name: String, date: String): Result<MenuResponse> {
        return try {
            val response = RetrofitInstance.api.getMenu(
                GraphQLRequest(
                    operationName = "getLocationMenu",
                    variables = Variables(name = name, date = date),
                    query = query
                )
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error fetching menu"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getItemDetails(itemId: String): Result<ItemDetailsResponse> {
        return try {
            val response = RetrofitInstance.api.getItemDetails(
                GraphQLRequest(
                    variables = ItemVariables(id = itemId),
                    query = itemQuery
                )
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error fetching item details"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
