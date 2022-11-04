package com.santik.repository;

import com.santik.TestDataManager;
import com.santik.api.model.RecipesSearch;
import com.santik.domain.Recipe;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SearchRecipeRepositoryTest extends TestDataManager {
  @Autowired
  ReactiveMongoSearchRecipeRepository recipeRepository;

  @Autowired
  RecipeSearchQueryBuilder recipeSearchQueryBuilder;

  @Test
  void search_withFullTextSearch() {
    //arrange
    var search = new RecipesSearch();
    search.setInstructionsSearch("potatoes");

    //act && assert
    StepVerifier.create(findBySearch(search))
        .expectNextCount(2)
        .verifyComplete();

    search.setIsVegetarian(true);
    StepVerifier.create(findBySearch(search))
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  void search_withIncludeExcludeIngredients() {
    //arrange
    var search = new RecipesSearch();
    search.setIncludeIngredients(List.of("potatoes"));

    //act && assert
    StepVerifier.create(findBySearch(search))
        .expectNextCount(2)
        .verifyComplete();

    search.setExcludeIngredients(List.of("meat"));
    StepVerifier.create(findBySearch(search))
        .expectNextCount(1)
        .verifyComplete();
  }


  @Test
  void search_withNumberOfServingsVegetarian() {
    //arrange
    var search = new RecipesSearch();
    search.setNumberOfServings(2);
    search.setIsVegetarian(true);

    //act && assert
    StepVerifier.create(findBySearch(search))
        .expectNextCount(1)
        .verifyComplete();
  }

  private Flux<Recipe> findBySearch(RecipesSearch search) {
    return recipeRepository.findAll(recipeSearchQueryBuilder.getRecipeSearchQuery(search));
  }
}