package com.santik.repository;

import com.santik.api.model.RecipesSearch;
import com.santik.domain.Recipe;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RecipeRepository {

  private final ReactiveMongoRecipeRepository reactiveMongoRecipeRepository;
  private final ReactiveMongoSearchRecipeRepository reactiveMongoSearchRecipeRepository;
  private final RecipeSearchQueryBuilder recipeSearchQueryBuilder;

  public Mono<Recipe> save(Recipe recipe) {
    return reactiveMongoRecipeRepository.save(recipe);
  }

  public Flux<Recipe> findAll() {
    return reactiveMongoRecipeRepository.findAll();
  }

  public Mono<Recipe> findById(String id) {
    return reactiveMongoRecipeRepository.findById(id);
  }

  public Mono<Void> deleteById(String id) {
    return reactiveMongoRecipeRepository.deleteById(id);
  }

  public Flux<Recipe> search(RecipesSearch recipesSearch) {
    Query recipeSearchQuery = recipeSearchQueryBuilder.getRecipeSearchQuery(recipesSearch);
    return reactiveMongoSearchRecipeRepository.findAll(recipeSearchQuery);
  }

  public Flux<Recipe> saveAll(List<Recipe> recipes) {
    return reactiveMongoRecipeRepository.saveAll(recipes);
  }

  public Mono<Void> deleteAll() {
    return reactiveMongoRecipeRepository.deleteAll();
  }
}
