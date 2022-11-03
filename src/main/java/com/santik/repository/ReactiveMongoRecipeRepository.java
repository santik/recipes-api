package com.santik.repository;

import com.santik.domain.Recipe;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

interface ReactiveMongoRecipeRepository extends ReactiveMongoRepository<Recipe, String> {

}
