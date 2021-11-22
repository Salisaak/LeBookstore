package com.example.bookstore.services;

import com.example.bookstore.models.Category;
import com.example.bookstore.repositories.BookRepository;
import com.example.bookstore.repositories.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    public ResponseEntity<Category> createCategory(Category category) {
        Category newCategory = categoryRepository.save(category);

        URI newCategoryUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(newCategory.getId()).toUri();

        logger.info("Category created");

        return ResponseEntity.created(newCategoryUri).body(newCategory);
    }

    public ResponseEntity<?> getAllCategories() {

        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            logger.info("No Categories Found");
            return ResponseEntity.notFound().build();
        }
        logger.info("Category found");
        return ResponseEntity.ok(categories);
    }

    public ResponseEntity<?> getCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            logger.info("Category missing");
            return ResponseEntity.notFound().build();
        }

        logger.info("Category found");
        return ResponseEntity.ok(category.get());
    }

    public ResponseEntity<?> updateCategory(Category category,Long id) {
        Optional<Category> findByCategoryId = categoryRepository.findById(id);
        if (findByCategoryId.isEmpty()) {
            logger.info("Category missing");
            return ResponseEntity.notFound().build();
        }

        logger.info("Category changed");
        category.setId(findByCategoryId.get().getId());
        categoryRepository.save(category);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> deleteCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            logger.info("Category missing");
            return ResponseEntity.notFound().build();
        }
        categoryRepository.deleteById(id);
        logger.info("Category removed");
        return ResponseEntity.accepted().build();
    }

    public ResponseEntity<?> getCategoryByBookId(Long bookId) {
        return ResponseEntity.ok(categoryRepository.findByBookId(bookId));
    }

    public ResponseEntity<?> getBookByKeyword(String name) {
        return ResponseEntity.ok(categoryRepository.findByName(name));
    }
}
