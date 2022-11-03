package com.santik.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document
public class Recipe {

  @Id
  private String id;

  private String name;

  private Boolean isVegetarian;

  private Integer numberOfServings;

  @TextIndexed(weight = 5)
  private String instructions;

  private List<String> ingredients;
}
