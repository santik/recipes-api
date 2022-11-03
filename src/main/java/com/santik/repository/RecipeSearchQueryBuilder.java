package com.santik.repository;

import com.santik.api.model.RecipesSearch;
import java.util.Objects;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Component;

@Component
class RecipeSearchQueryBuilder {

  public Query getRecipeSearchQuery(RecipesSearch search) {
    Query query = new Query();

    if (Objects.nonNull(search.getIsVegetarian())) {
      query.addCriteria(Criteria.where("isVegetarian").is(search.getIsVegetarian()));
    }
    if (Objects.nonNull(search.getInstructionsSearch())) {
      query.addCriteria(TextCriteria.forDefaultLanguage().matching(search.getInstructionsSearch()));
    }
    if (Objects.nonNull(search.getNumberOfServings())) {
      query.addCriteria(Criteria.where("numberOfServings").is(search.getNumberOfServings()));
    }
    if (Objects.nonNull(search.getExcludeIngredients()) || Objects.nonNull(
        search.getIncludeIngredients())) {
      Criteria ingredientsCriteria = Criteria.where("ingredients");
      if (Objects.nonNull(search.getExcludeIngredients())) {
        ingredientsCriteria.nin(search.getExcludeIngredients());
      }
      if (Objects.nonNull(search.getIncludeIngredients())) {
        ingredientsCriteria.in(search.getIncludeIngredients());
      }
      query.addCriteria(ingredientsCriteria);
    }
    return query;
  }

}
