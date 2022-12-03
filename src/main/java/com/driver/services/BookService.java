package com.driver.services;

import com.driver.models.Author;
import com.driver.models.Book;
import com.driver.models.Genre;
import com.driver.repositories.AuthorRepository;
import com.driver.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

@Autowired
    AuthorRepository authorRepository;
    @Autowired
    BookRepository bookRepository2;

    public void createBook(Book book)
    {
//        Author author=authorRepository.findById(2).get();
//        book.setAuthor(author);

        //book.setGenre(Genre.FICTIONAL);
        bookRepository2.save(book);
    }

    public List<Book> getBooks(String genre, boolean available, String author){
        List<Book> books = null; //find the elements of the list by yourself

        books.addAll(bookRepository2.findBooksByAuthor(author,available));
        books.addAll(bookRepository2.findBooksByGenre(genre,available));
        books.addAll(bookRepository2.findBooksByGenreAuthor(genre, author, available));
        books.addAll(bookRepository2.findByAvailability(available));


        return books;
    }
}
