package com.example.bookstore.services;

import com.example.bookstore.models.Book;
import com.example.bookstore.models.Category;
import com.example.bookstore.repositories.BookRepository;
import com.example.bookstore.repositories.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import java.util.List;
import java.util.Optional;

@Service
public class BookService{

    @Autowired
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);


    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    public ResponseEntity<?> getAllBooks() {
        List<Book> books = bookRepository.findAll();

        if (books.isEmpty()) {
            logger.info("Books missing");
            return ResponseEntity.noContent().build();
        }
        logger.info("Books found");
        return ResponseEntity.ok(books);
    }

    public ResponseEntity<?> getBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (bookRepository.findById(id).isEmpty()) {
            logger.info("Book missing");
            return ResponseEntity.noContent().build();
        }
        logger.info("Book found");
        return ResponseEntity.ok(book.get());
    }

    public ResponseEntity<?> updateBook(Book book, Long id) {
        Optional<Category> categories = categoryRepository
                .findById(book.getCategory().getId());
        Optional<Book> books = bookRepository.findById(id);

        if (bookRepository.findById(id).isEmpty()) {
            logger.info("Book missing");
            return ResponseEntity.notFound().build();
        }

        book.setCategory(categories.get());
        book.setId(books.get().getId());

        bookRepository.save(book);

        logger.info("Book updated");
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> deleteBook(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            logger.info("Book missing");
            return ResponseEntity.noContent().build();
        }

        logger.info("Book burned");
        bookRepository.delete(book.get());
        return ResponseEntity.ok().build();

    }

    public ResponseEntity<?> createBook(Book book) {
        Optional<Category> categories = categoryRepository
                .findById(book.getCategory().getId());

        if (categories.isEmpty()) {
            logger.info("Category is missing");
            return ResponseEntity.noContent().build();
        }

        book.setCategory(categories.get());
        Book newBook = bookRepository.save(book);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(newBook.getId()).toUri();

        logger.info("Book written");
        return ResponseEntity.created(location).body(newBook);
    }

    public ResponseEntity<?>getBookByCategoryId(Long id) {
        return ResponseEntity.ok(bookRepository.findByCategoryId(id));
    }

    public ResponseEntity<?>getBookByCategoryName(String name) {
        return ResponseEntity.ok(bookRepository.findByCategoryNameContainsIgnoreCase(name));
    }
}
