package com.santik.repository;

import com.santik.domain.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
class ReactiveMongoSearchRecipeRepository {

  private final ReactiveMongoTemplate mongoTemplate;

  public Flux<Recipe> findAll(Query query) {
    return mongoTemplate.find(query, Recipe.class);
  }
}
