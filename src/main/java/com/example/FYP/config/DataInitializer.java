package com.example.FYP.config;

import com.example.FYP.model.AacCategory;
import com.example.FYP.repository.AacCategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(AacCategoryRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                List<AacCategory> standardCategories = Arrays.asList(
                    AacCategory.builder().name("Food & Drink").iconUrl("https://cdn-icons-png.flaticon.com/512/3075/3075977.png").build(),
                    AacCategory.builder().name("Feelings & Emotions").iconUrl("https://cdn-icons-png.flaticon.com/512/1933/1933611.png").build(),
                    AacCategory.builder().name("Common Actions").iconUrl("https://cdn-icons-png.flaticon.com/512/3048/3048381.png").build(),
                    AacCategory.builder().name("Social Phrases").iconUrl("https://cdn-icons-png.flaticon.com/512/1189/1189138.png").build(),
                    AacCategory.builder().name("Objects").iconUrl("https://cdn-icons-png.flaticon.com/512/763/763789.png").build(),
                    AacCategory.builder().name("People").iconUrl("https://cdn-icons-png.flaticon.com/512/4140/4140037.png").build()
                );
                repository.saveAll(standardCategories);
            }
        };
    }
}
