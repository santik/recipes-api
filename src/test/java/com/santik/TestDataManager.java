package com.santik;

import com.santik.domain.Recipe;
import com.santik.repository.ReactiveMongoRecipeRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class TestDataManager {

  protected static final String EXISTING_ID = UUID.randomUUID().toString();
  @Autowired
  ReactiveMongoRecipeRepository recipeRepository;

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

    recipeRepository.saveAll(recipes)
        .blockLast();
  }

  @AfterEach
  void tearDown() {
    recipeRepository.deleteAll().block();
  }
}
