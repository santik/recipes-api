package com.santik.repository;

import com.santik.api.model.RecipesSearch;
import com.santik.domain.Recipe;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SearchRecipeRepositoryTest {

  public static final String EXISTING_ID = UUID.randomUUID().toString();

  @Autowired
  ReactiveMongoRecipeRepository reactiveMongoRecipeRepository;
  @Autowired
  RecipeRepository recipeRepository;

  @BeforeEach
  void setUp() {
    var recipes = List.of(
        new Recipe(null, "recipe1", false, 1, "hard instructions1",
            List.of("salt", "ingredient12")),
        new Recipe(null, "recipe2", true, 2, "instructions2 easy",
            List.of("salt", "sugar")),
        new Recipe(null, "recipe2", false, 2, "instructions2 easy",
            List.of("salt", "ingredient22")),
        new Recipe(EXISTING_ID, "recipe3", false, 3, "instructions3 difficult",
            List.of("ingredient31", "ingredient32"))
    );

    reactiveMongoRecipeRepository.saveAll(recipes)
        .blockLast();
  }

  @AfterEach
  void tearDown() {
    reactiveMongoRecipeRepository.deleteAll().block();
  }


  @Test
  void search_withFullTextSearch() {
    //arrange
    RecipesSearch search = new RecipesSearch();
    search.setInstructionsSearch("easy");

    //act
    Flux<Recipe> recipeFlux = recipeRepository.search(search).log();

    //assert
    StepVerifier.create(recipeFlux)
        .expectNextCount(2)
        .verifyComplete();

    search.setIsVegetarian(true);
    StepVerifier.create(recipeRepository.search(search))
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  void search_withIncludeExcludeIngredients() {
    //arrange
    RecipesSearch search = new RecipesSearch();
    search.setIncludeIngredients(List.of("salt"));

    //act && assert
    StepVerifier.create(recipeRepository.search(search))
        .expectNextCount(3)
        .verifyComplete();

    search.setExcludeIngredients(List.of("sugar"));
    StepVerifier.create(recipeRepository.search(search))
        .expectNextCount(2)
        .verifyComplete();
  }

  @Test
  void search_withNumberOfServingsVegetarian() {
    //arrange
    RecipesSearch search = new RecipesSearch();
    search.setNumberOfServings(2);

    //act && assert
    StepVerifier.create(recipeRepository.search(search))
        .expectNextCount(2)
        .verifyComplete();

    search.setIsVegetarian(true);
    StepVerifier.create(recipeRepository.search(search))
        .expectNextCount(1)
        .verifyComplete();
  }
}