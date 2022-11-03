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

  private static final String EXISTING_ID = UUID.randomUUID().toString();
  @Autowired
  ReactiveMongoRecipeRepository reactiveMongoRecipeRepository;

  @BeforeEach
  void setUp() {
    var recipes = List.of(
        new Recipe(EXISTING_ID, "pasta carbonara", false, 3, "take pasta, add carbonara",
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
  void findAll_shouldReturnAllRecipes() {
    //act
    var all = reactiveMongoRecipeRepository.findAll();
    //assert
    StepVerifier.create(all)
        .expectNextCount(3)
        .verifyComplete();
  }

  @Test
  void findById() {
    //act
    var recipeMono = reactiveMongoRecipeRepository.findById(EXISTING_ID);
    //assert
    StepVerifier.create(recipeMono)
        .assertNext(recipe -> assertEquals("pasta carbonara", recipe.getName()))
        .verifyComplete();
  }

  @Test
  void saveRecipe() {
    //arrange
    var recipe = new Recipe(null, "recipe", false, 1, "instructions",
        List.of("ingredient"));
    //act
    var save = reactiveMongoRecipeRepository.save(recipe);
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
    var recipe = reactiveMongoRecipeRepository.findById(EXISTING_ID).block();
    recipe.setNumberOfServings(2022);
    //act
    var save = reactiveMongoRecipeRepository.save(recipe);
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
    var all = reactiveMongoRecipeRepository.findAll();
    //assert
    StepVerifier.create(all)
        .expectNextCount(2)
        .verifyComplete();

  }
}