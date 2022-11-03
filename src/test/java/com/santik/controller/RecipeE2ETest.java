package com.santik.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.santik.api.model.RecipesSearch;
import com.santik.domain.Recipe;
import com.santik.repository.RecipeRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class RecipeE2ETest {

  private static final String RECIPE_URI = "/recipes";
  private static final String SEARCHES_PATH = "/searches";
  private static final String EXISTING_ID = UUID.randomUUID().toString();
  @Autowired
  RecipeRepository recipeRepository;

  @Autowired
  WebTestClient webTestClient;

  @BeforeEach
  void setUp() {

    var recipes = List.of(
        new Recipe(null, "recipe1", false, 1, "hard instructions1",
            List.of("ingredient11", "ingredient12")),
        new Recipe(null, "recipe2", true, 2, "easy instructions2",
            List.of("ingredient21", "ingredient22")),
        new Recipe(EXISTING_ID, "recipe3", false, 3, "medium instructions3",
            List.of("ingredient31", "ingredient32"))
    );

    recipeRepository.saveAll(recipes)
        .blockLast();
  }

  @AfterEach
  void tearDown() {
    recipeRepository.deleteAll().block();
  }

  @Test
  void addRecipe() {

    Recipe recipe = new Recipe(null, "recipe1", false, 1, "instructions1",
        List.of("ingredient11", "ingredient12"));

    webTestClient
        .post()
        .uri(RECIPE_URI)
        .bodyValue(recipe)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(Recipe.class)
        .consumeWith(recipeEntityExchangeResult -> {
          Recipe responseBody = recipeEntityExchangeResult.getResponseBody();
          assert responseBody != null;
          assertNotNull(responseBody.getId());
        });
  }

  @Test
  void addRecipe_badRequest() {

    Recipe recipe = new Recipe(null, "", false, 1, "instructions1",
        List.of("ingredient11", "ingredient12"));

    webTestClient
        .post()
        .uri(RECIPE_URI)
        .bodyValue(recipe)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void getAllRecipes() {
    webTestClient
        .get()
        .uri(RECIPE_URI)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Recipe.class)
        .hasSize(3);
  }

  @Test
  void getRecipesById() {
    webTestClient
        .get()
        .uri(RECIPE_URI + "/" + EXISTING_ID)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody()
        .jsonPath("$.name").isEqualTo("recipe3");
  }

  @Test
  void getRecipesById_404() {
    webTestClient
        .get()
        .uri(RECIPE_URI + "/" + EXISTING_ID + "1")
        .exchange()
        .expectStatus()
        .isNotFound();
  }


  @Test
  void updateRecipe() {

    Recipe recipe = new Recipe(null, "recipe1", false, 1, "instructions1",
        List.of("ingredient11", "ingredient12"));

    webTestClient
        .put()
        .uri(RECIPE_URI + "/" + EXISTING_ID)
        .bodyValue(recipe)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(Recipe.class)
        .consumeWith(recipeEntityExchangeResult -> {
          Recipe responseBody = recipeEntityExchangeResult.getResponseBody();
          assert responseBody != null;
          assertNotNull(responseBody.getId());
          assertEquals(recipe.getName(), responseBody.getName());
        });
  }

  @Test
  void updateRecipe_badRequest() {

    Recipe recipe = new Recipe(null, "recipe1", false, -1, "instructions1",
        List.of("ingredient11", "ingredient12"));

    webTestClient
        .put()
        .uri(RECIPE_URI + "/" + EXISTING_ID)
        .bodyValue(recipe)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void updateRecipe_notFound() {

    Recipe recipe = new Recipe(null, "recipe1", false, 1, "instructions1",
        List.of("ingredient11", "ingredient12"));

    webTestClient
        .put()
        .uri(RECIPE_URI + "/" + EXISTING_ID + "1")
        .bodyValue(recipe)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void deleteRecipe() {

    webTestClient
        .delete()
        .uri(RECIPE_URI + "/" + EXISTING_ID)
        .exchange()
        .expectStatus()
        .isNoContent();

    webTestClient
        .get()
        .uri(RECIPE_URI + "/" + EXISTING_ID)
        .exchange()
        .expectStatus()
        .isNotFound();
    ;
  }

  @Test
  void searchRecipes() {

    RecipesSearch recipeSearch = new RecipesSearch();
    recipeSearch.setInstructionsSearch("hard");
    webTestClient
        .post()
        .uri(RECIPE_URI + "/searches")
        .bodyValue(recipeSearch)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Recipe.class)
        .hasSize(1);

    recipeSearch.setIsVegetarian(true);
    webTestClient
        .post()
        .uri(RECIPE_URI + "/searches")
        .bodyValue(recipeSearch)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Recipe.class)
        .hasSize(0);

  }

  @Test
  void searchRecipes_badRequest() {

    RecipesSearch recipeSearch = new RecipesSearch();
    recipeSearch.setInstructionsSearch("");

    webTestClient
        .post()
        .uri(RECIPE_URI + SEARCHES_PATH)
        .bodyValue(recipeSearch)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }
}