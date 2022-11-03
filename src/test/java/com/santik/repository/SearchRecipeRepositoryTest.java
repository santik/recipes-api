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

  private static final String EXISTING_ID = UUID.randomUUID().toString();

  @Autowired
  ReactiveMongoRecipeRepository reactiveMongoRecipeRepository;
  @Autowired
  RecipeRepository recipeRepository;

  @BeforeEach
  void setUp() {
    var recipes = List.of(
        new Recipe(EXISTING_ID, "pasta carbonara", false, 2, "take pasta, add carbonara",
            List.of("pasta", "carbonara")),
        new Recipe(null, "potatoes with meat", false, 4, "take potatoes, add meat, put in the oven",
            List.of("potatoes", "meat")),
        new Recipe(null, "potatoes with salmon", true, 2, "take potatoes, add fish, put in the oven",
            List.of("potatoes", "salmon"))
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
    var search = new RecipesSearch();
    search.setInstructionsSearch("potatoes");

    //act
    var recipeFlux = recipeRepository.search(search).log();

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
    var search = new RecipesSearch();
    search.setIncludeIngredients(List.of("potatoes"));

    //act && assert
    StepVerifier.create(recipeRepository.search(search))
        .expectNextCount(2)
        .verifyComplete();

    search.setExcludeIngredients(List.of("meat"));
    StepVerifier.create(recipeRepository.search(search))
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  void search_withNumberOfServingsVegetarian() {
    //arrange
    var search = new RecipesSearch();
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