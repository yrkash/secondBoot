package ru.alishev.springcourse.secondBoot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alishev.springcourse.secondBoot.models.Book;
import ru.alishev.springcourse.secondBoot.models.Person;
import ru.alishev.springcourse.secondBoot.repositories.BooksRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll() {
        return booksRepository.findAll();
    }
    public List<Book> findAll(String name) {
        return booksRepository.findAll(Sort.by(name));
    }
    public List<Book> findAll(int page, int booksPerPage) {
        return booksRepository.findAll(PageRequest.of(page,booksPerPage)).getContent();
    }

    public List<Book> findAll(int page, int booksPerPage, String name) {
        return booksRepository.findAll(PageRequest.of(page,booksPerPage, Sort.by(name))).getContent();
    }

    public Book findOne(int id) {
        Optional<Book> foundBook = booksRepository.findById(id);
        return foundBook.orElse(null);
    }

    @Transactional
    public void save(Book book) {

        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        updatedBook.setId(id);
        booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    public Person findByIdFetchBook(int id) {
        Optional<Book> foundBook = booksRepository.findById(id);
        if (foundBook.isPresent()) {
            return foundBook.get().getOwner();
        }
        return null;
    }

    @Transactional
    public void release(int id) {
        Optional<Book> foundBook = booksRepository.findById(id);
        if (foundBook.isPresent()) {
            foundBook.get().setOwner(null);
            foundBook.get().setDate(null);
            booksRepository.save(foundBook.get());
        }
    }

    @Transactional
    // Назначает книгу человеку (этот метод вызывается, когда человек забирает книгу из библиотеки)
    public void assign(int id, Person selectedPerson) {
        Optional<Book> foundBook = booksRepository.findById(id);
        if (foundBook.isPresent()) {
            foundBook.get().setOwner(selectedPerson);
            foundBook.get().setDate(new Date());
            booksRepository.save(foundBook.get());
        }
    }

    public List<Book> findByName(String title) {
        //probably business logic in this method
        return booksRepository.findByTitleStartingWith(title);
    }

    /*public List<Book> findByOwner(Person owner) {
        return booksRepository.findByOwner(owner);
    }*/

}
