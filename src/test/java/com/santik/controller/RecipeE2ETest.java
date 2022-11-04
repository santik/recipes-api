package com.santik.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.santik.TestDataManager;
import com.santik.api.model.RecipesSearch;
import com.santik.domain.Recipe;
import java.util.List;
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
class RecipeE2ETest extends TestDataManager {

  private static final String RECIPE_URI = "/recipes";
  private static final String SEARCHES_PATH = "/searches";

  @Autowired
  WebTestClient webTestClient;

  @Test
  void addRecipe() {

    var recipe = new Recipe(null, "recipe1", false, 1, "instructions1",
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
          var responseBody = recipeEntityExchangeResult.getResponseBody();
          assert responseBody != null;
          assertNotNull(responseBody.getId());
        });
  }

  @Test
  void addRecipe_badRequest() {

    var recipe = new Recipe(null, "", false, 1, "instructions",
        List.of("ingredient"));

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
        .jsonPath("$.name").isEqualTo("pasta carbonara");
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

    var recipe = new Recipe(null, "recipe", false, 1, "instructions",
        List.of("ingredient"));

    webTestClient
        .put()
        .uri(RECIPE_URI + "/" + EXISTING_ID)
        .bodyValue(recipe)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(Recipe.class)
        .consumeWith(recipeEntityExchangeResult -> {
          var responseBody = recipeEntityExchangeResult.getResponseBody();
          assert responseBody != null;
          assertNotNull(responseBody.getId());
          assertEquals(recipe.getName(), responseBody.getName());
        });
  }

  @Test
  void updateRecipe_badRequest() {

    var recipe = new Recipe(null, "recipe", false, -1, "instructions",
        List.of("ingredient"));

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

    var recipe = new Recipe(null, "recipe", false, 1, "instructions",
        List.of("ingredient"));

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

    var recipeSearch = new RecipesSearch();
    recipeSearch.setInstructionsSearch("pasta");
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
  void searchRecipes_allVegetarian() {

    var recipeSearch = new RecipesSearch();
    recipeSearch.setIsVegetarian(true);
    webTestClient
        .post()
        .uri(RECIPE_URI + "/searches")
        .bodyValue(recipeSearch)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Recipe.class)
        .hasSize(1);
  }

  @Test
  void searchRecipes_4PersonPotatoes() {

    var recipeSearch = new RecipesSearch();
    recipeSearch.setNumberOfServings(4);
    recipeSearch.setInstructionsSearch("potatoes");
    webTestClient
        .post()
        .uri(RECIPE_URI + "/searches")
        .bodyValue(recipeSearch)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Recipe.class)
        .hasSize(1);
  }

  @Test
  void searchRecipes_NoSalmonOven() {

    var recipeSearch = new RecipesSearch();
    recipeSearch.setExcludeIngredients(List.of("salmon"));
    recipeSearch.setInstructionsSearch("oven");
    webTestClient
        .post()
        .uri(RECIPE_URI + "/searches")
        .bodyValue(recipeSearch)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Recipe.class)
        .hasSize(1);
  }

  @Test
  void searchRecipes_badRequest() {

    var recipeSearch = new RecipesSearch();
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