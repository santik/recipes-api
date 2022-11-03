package com.santik.mapper;

import com.santik.domain.Recipe;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecipesMapper {

  Recipe map(com.santik.api.model.Recipe recipe);

  Recipe map(com.santik.api.model.NewRecipe recipe);

  com.santik.api.model.Recipe map(Recipe recipe);
}
