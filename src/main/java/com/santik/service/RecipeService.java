package com.santik.service;

import com.santik.api.model.NewRecipe;
import com.santik.api.model.Recipe;
import com.santik.api.model.RecipesSearch;
import com.santik.mapper.RecipesMapper;
import com.santik.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RecipeService {

  private final RecipesMapper mapper;
  private final RecipeRepository recipeRepository;

  public Mono<Recipe> addRecipe(NewRecipe recipe) {
    return recipeRepository
        .save(mapper.map(recipe))
        .map(mapper::map);
  }

  public Flux<Recipe> getAllRecipes() {
    return recipeRepository.findAll().map(mapper::map);
  }

  public Mono<Recipe> getRecipeById(String id) {
    return recipeRepository.findById(id).map(mapper::map);
  }

  public Mono<Recipe> updateRecipeById(String id, Mono<Recipe> monoRecipe) {
    return recipeRepository.findById(id)
        .flatMap(fetchedRecipe ->
            monoRecipe
                .flatMap(recipe -> {
                  fetchedRecipe.setName(recipe.getName());
                  fetchedRecipe.setIsVegetarian(recipe.getIsVegetarian());
                  fetchedRecipe.setNumberOfServings(recipe.getNumberOfServings());
                  fetchedRecipe.setInstructions(recipe.getInstructions());
                  fetchedRecipe.setIngredients(recipe.getIngredients());
                  return recipeRepository.save(fetchedRecipe);
                }))
        .map(mapper::map);

  }

  public Mono<Void> deleteRecipeById(String id) {
    return recipeRepository.deleteById(id);
  }

  public Flux<Recipe> searchRecipes(Mono<RecipesSearch> recipesSearch) {
    return recipesSearch
        .flatMapMany(recipeRepository::search)
        .map(mapper::map);
  }
}
