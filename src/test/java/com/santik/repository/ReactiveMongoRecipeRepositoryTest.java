package com.santik.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.santik.TestDataManager;
import com.santik.domain.Recipe;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
class ReactiveMongoRecipeRepositoryTest extends TestDataManager {

  @Autowired
  ReactiveMongoRecipeRepository reactiveMongoRecipeRepository;

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