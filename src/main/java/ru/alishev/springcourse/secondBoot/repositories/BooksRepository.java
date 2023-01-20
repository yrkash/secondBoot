
package ru.alishev.springcourse.secondBoot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alishev.springcourse.secondBoot.models.Book;
import ru.alishev.springcourse.secondBoot.models.Person;


import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {
    List<Book> findByTitle(String title);

    List<Book> findByOwner(Person owner);

    List<Book> findByTitleStartingWith(String title);
//    Book findByTitleIsLike(String title);
}

