package com.santik.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.santik.api.model.RecipesSearch;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

class RecipeSearchQueryBuilderTest {

  @Test
  void getRecipeSearchQuery() {
    //arrange
    RecipeSearchQueryBuilder builder = new RecipeSearchQueryBuilder();

    RecipesSearch search = new RecipesSearch();
    search.setIsVegetarian(true);
    search.setNumberOfServings(123);
    search.setInstructionsSearch("somesearch");
    search.setExcludeIngredients(List.of("ExcludeIngredients"));
    search.setIncludeIngredients(List.of("IncludeIngredients"));

    //act
    Query recipeSearchQuery = builder.getRecipeSearchQuery(search);

    //assert
    Map<String, CriteriaDefinition> criteria = (Map<String, CriteriaDefinition>) ReflectionTestUtils.getField(
        recipeSearchQuery, "criteria");

    assertTrue(criteria.containsKey("isVegetarian"));
    Boolean isVegetarian = criteria.get("isVegetarian").getCriteriaObject()
        .get("isVegetarian", Boolean.class);
    assertTrue(isVegetarian);

    assertTrue(criteria.containsKey("numberOfServings"));
    Integer numberOfServings = criteria.get("numberOfServings").getCriteriaObject()
        .get("numberOfServings", Integer.class);
    assertEquals(search.getNumberOfServings(), numberOfServings);

    assertTrue(criteria.containsKey("ingredients"));
    assertTrue(criteria.containsKey("$text"));
  }
}