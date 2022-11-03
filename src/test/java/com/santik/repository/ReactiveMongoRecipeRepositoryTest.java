package com.santik.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.santik.domain.Recipe;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
class ReactiveMongoRecipeRepositoryTest {

  public static final String EXISTING_ID = UUID.randomUUID().toString();
  @Autowired
  ReactiveMongoRecipeRepository reactiveMongoRecipeRepository;

  @BeforeEach
  void setUp() {
    var recipes = List.of(
        new Recipe(null, "recipe1", false, 1, "instructions1",
            List.of("ingredient11", "ingredient12")),
        new Recipe(null, "recipe2", true, 2, "instructions2",
            List.of("ingredient21", "ingredient22")),
        new Recipe(EXISTING_ID, "recipe3", false, 3, "instructions3",
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
  void findAll_shouldReturnAllRecipes() {
    //act
    Flux<Recipe> all = reactiveMongoRecipeRepository.findAll();
    //assert
    StepVerifier.create(all)
        .expectNextCount(3)
        .verifyComplete();
  }

  @Test
  void findById() {
    //act
    Mono<Recipe> recipeMono = reactiveMongoRecipeRepository.findById(EXISTING_ID);
    //assert
    StepVerifier.create(recipeMono)
        .assertNext(recipe -> assertEquals("recipe3", recipe.getName()))
        .verifyComplete();
  }

  @Test
  void saveRecipe() {
    //arrange
    Recipe recipe = new Recipe(null, "recipe1", false, 1, "instructions1",
        List.of("ingredient11", "ingredient12"));
    //act
    Mono<Recipe> save = reactiveMongoRecipeRepository.save(recipe);
    //assert
    StepVerifier.create(save)
        .assertNext(savedRecipe -> {
          assertNotNull(savedRecipe.getId());
          assertEquals(recipe.getName(), savedRecipe.getName());
        })
        .verifyComplete();
  }

  @Test
  void update() {
    //arrange
    Recipe recipe = reactiveMongoRecipeRepository.findById(EXISTING_ID).block();
    recipe.setNumberOfServings(2022);
    //act
    Mono<Recipe> save = reactiveMongoRecipeRepository.save(recipe);
    //assert
    StepVerifier.create(save)
        .assertNext(savedRecipe -> {
          assertEquals(recipe.getNumberOfServings(), savedRecipe.getNumberOfServings());
          assertEquals(savedRecipe.getName(), recipe.getName());
        })
        .verifyComplete();
  }

  @Test
  void delete() {
    //arrange
    reactiveMongoRecipeRepository.deleteById(EXISTING_ID).block();
    //act
    Flux<Recipe> all = reactiveMongoRecipeRepository.findAll();
    //assert
    StepVerifier.create(all)
        .expectNextCount(2)
        .verifyComplete();

  }
}