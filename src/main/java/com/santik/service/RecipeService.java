package com.santik.service;

import com.santik.api.model.NewRecipe;
import com.santik.api.model.Recipe;
import com.santik.api.model.RecipesSearch;
import com.santik.mapper.RecipesMapper;
import com.santik.repository.ReactiveMongoRecipeRepository;
import com.santik.repository.ReactiveMongoSearchRecipeRepository;
import com.santik.repository.RecipeSearchQueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RecipeService {

  private final RecipesMapper mapper;
  private final ReactiveMongoRecipeRepository reactiveMongoRecipeRepository;
  private final ReactiveMongoSearchRecipeRepository reactiveMongoSearchRecipeRepository;
  private final RecipeSearchQueryBuilder recipeSearchQueryBuilder;

  public Mono<Recipe> addRecipe(NewRecipe recipe) {
    return reactiveMongoRecipeRepository
        .save(mapper.map(recipe))
        .map(mapper::map);
  }

  public Flux<Recipe> getAllRecipes() {
    return reactiveMongoRecipeRepository.findAll().map(mapper::map);
  }

  public Mono<Recipe> getRecipeById(String id) {
    return reactiveMongoRecipeRepository.findById(id).map(mapper::map);
  }

  public Mono<Recipe> updateRecipeById(String id, Mono<Recipe> monoRecipe) {
    return reactiveMongoRecipeRepository.findById(id)
        .flatMap(fetchedRecipe ->
            monoRecipe
                .flatMap(recipe -> {
                  fetchedRecipe.setName(recipe.getName());
                  fetchedRecipe.setIsVegetarian(recipe.getIsVegetarian());
                  fetchedRecipe.setNumberOfServings(recipe.getNumberOfServings());
                  fetchedRecipe.setInstructions(recipe.getInstructions());
                  fetchedRecipe.setIngredients(recipe.getIngredients());
                  return reactiveMongoRecipeRepository.save(fetchedRecipe);
                }))
        .map(mapper::map);

  }

  public Mono<Void> deleteRecipeById(String id) {
    return reactiveMongoRecipeRepository.deleteById(id);
  }

  public Flux<Recipe> searchRecipes(Mono<RecipesSearch> recipesSearch) {
    return recipesSearch
        .map(recipeSearchQueryBuilder::getRecipeSearchQuery)
        .flatMapMany(reactiveMongoSearchRecipeRepository::findAll)
        .map(mapper::map);
  }
}
