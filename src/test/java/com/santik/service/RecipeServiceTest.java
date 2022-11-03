package com.santik.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.santik.api.model.NewRecipe;
import com.santik.api.model.RecipesSearch;
import com.santik.domain.Recipe;
import com.santik.mapper.RecipesMapper;
import com.santik.repository.RecipeRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

  @Mock
  RecipeRepository recipeRepository;

  @Mock
  RecipesMapper recipesMapper;

  @InjectMocks
  RecipeService recipeService;
  private NewRecipe newRecipe;
  private com.santik.api.model.Recipe recipe;
  private Recipe domainRecipe;


  @BeforeEach
  void setUp() {
    newRecipe = new NewRecipe();
    recipe = new com.santik.api.model.Recipe();
    domainRecipe = new Recipe(null, "recipe1", false, 1, "instructions1",
        List.of("ingredient11", "ingredient12"));

    when(recipesMapper.map(domainRecipe)).thenReturn(recipe);
  }

  @Test
  void addRecipe() {
    //act
    when(recipesMapper.map(newRecipe)).thenReturn(domainRecipe);
    when(recipeRepository.save(domainRecipe)).thenReturn(Mono.just(domainRecipe));
    Mono<com.santik.api.model.Recipe> recipeMono = recipeService.addRecipe(newRecipe);

    //assert
    assertSame(recipeMono.block(), recipe);
  }

  @Test
  void getAllRecipes() {
    //arrange
    when(recipeRepository.findAll()).thenReturn(Flux.just(domainRecipe));

    //act
    Flux<com.santik.api.model.Recipe> allRecipes = recipeService.getAllRecipes();

    //assert
    assertSame(allRecipes.blockFirst(), recipe);
  }

  @Test
  void getRecipeById() {
    //arrange
    when(recipeRepository.findById(anyString())).thenReturn(Mono.just(domainRecipe));
    String id = "someid";

    //act
    Mono<com.santik.api.model.Recipe> recipe = recipeService.getRecipeById(id);

    //assert
    assertSame(recipe.block(), this.recipe);
  }

  @Test
  void updateRecipeById() {
    //arrange
    String id = "someid";
    when(recipeRepository.save(domainRecipe)).thenReturn(Mono.just(domainRecipe));
    when(recipeRepository.findById(id)).thenReturn(Mono.just(domainRecipe));

    //act
    Mono<com.santik.api.model.Recipe> recipeMono = recipeService.updateRecipeById(id,
        Mono.just(recipe));

    //assert
    assertSame(recipeMono.block(), recipe);
  }

  @Test
  void deleteRecipeById() {
    //arrange
    String id = "someid";
    when(recipeRepository.deleteById(id)).thenReturn(Mono.empty());
    reset(recipesMapper);

    //act
    recipeService.deleteRecipeById(id).block();
  }

  @Test
  void search() {
    //arrange
    RecipesSearch search = new RecipesSearch();
    when(recipeRepository.search(search)).thenReturn(Flux.just(domainRecipe));

    //act && assert
    assertSame(recipe, recipeService.searchRecipes(Mono.just(search)).blockFirst());
  }
}