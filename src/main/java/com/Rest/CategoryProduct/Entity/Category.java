package com.Rest.CategoryProduct.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("category_id") // Maps a JSON field to a Java property (and vice versa).
    private long categoryId;
//    @Column( unique = true)
    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @JsonProperty("brands")
    @JsonManagedReference  // Used to handle bi-directional relationships during JSON serialization to prevent infinite recursion.
    private List<Brand> brands;
}
