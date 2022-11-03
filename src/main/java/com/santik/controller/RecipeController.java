package com.santik.controller;

import com.santik.api.RecipesApi;
import com.santik.api.model.NewRecipe;
import com.santik.api.model.Recipe;
import com.santik.api.model.RecipesSearch;
import com.santik.service.RecipeService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class RecipeController implements RecipesApi {

  private final RecipeService recipeService;

  @Override
  public Mono<ResponseEntity<Recipe>> addRecipe(@Valid Mono<NewRecipe> newRecipe,
      ServerWebExchange exchange) {
    return newRecipe
        .flatMap(recipeService::addRecipe)
        .map(recipe -> ResponseEntity.status(HttpStatus.CREATED).body(recipe));
  }

  @Override
  public Mono<ResponseEntity<Void>> deleteRecipe(String id, ServerWebExchange exchange) {
    return recipeService.deleteRecipeById(id)
        .then(Mono.just(ResponseEntity.noContent().build()));
  }

  @Override
  public Mono<ResponseEntity<Recipe>> findRecipeById(String id, ServerWebExchange exchange) {
    return recipeService.getRecipeById(id)
        .map(ResponseEntity::ok)
        .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  @Override
  public Mono<ResponseEntity<Flux<Recipe>>> findRecipes(ServerWebExchange exchange) {
    return Mono.just(ResponseEntity.ok(recipeService.getAllRecipes()));
  }

  @Override
  public Mono<ResponseEntity<Recipe>> updateRecipe(String id, @Valid Mono<Recipe> recipe,
      ServerWebExchange exchange) {
    return recipeService.updateRecipeById(id, recipe)
        .map(ResponseEntity::ok)
        .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  @Override
  public Mono<ResponseEntity<Flux<Recipe>>> searchRecipes(Mono<RecipesSearch> recipesSearch,
      ServerWebExchange exchange) {
    return Mono.just(ResponseEntity.ok(recipeService.searchRecipes(recipesSearch)));
  }
}
