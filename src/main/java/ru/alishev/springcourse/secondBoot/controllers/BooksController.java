package ru.alishev.springcourse.secondBoot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.alishev.springcourse.secondBoot.models.Book;
import ru.alishev.springcourse.secondBoot.models.Person;
import ru.alishev.springcourse.secondBoot.services.BooksService;
import ru.alishev.springcourse.secondBoot.services.PeopleService;


import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final PeopleService peopleService;
    private final BooksService booksService;


    @Autowired
    public BooksController(PeopleService peopleService, BooksService booksService) {
        this.peopleService = peopleService;
        this.booksService = booksService;
    }

    @GetMapping()
    public String index(Model model,
                        @RequestParam(value = "sortByYear", defaultValue = "false") Boolean sortByYear,
                        @RequestParam(value = "page", defaultValue = "0" , required = false) int page,
                        @RequestParam(value = "books_per_page", defaultValue = "0", required = false) int booksPerPage) {
        if (sortByYear == false && booksPerPage == 0) {
            System.out.println("Обычный вывод");
            model.addAttribute("books", booksService.findAll());

        }
        if (sortByYear && booksPerPage == 0) {
            System.out.println("Только сортировка");
            model.addAttribute("books", booksService.findAll("year"));
        }
        if (sortByYear == false && booksPerPage != 0) {
            System.out.println("Только пагинация");
            model.addAttribute("books", booksService.findAll(page, booksPerPage));
        }
        if (sortByYear && booksPerPage != 0) {
            System.out.println("Combo");
            model.addAttribute("books", booksService.findAll(page, booksPerPage, "year"));
        }

        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
//        model.addAttribute("book", bookDAO.show(id));
        model.addAttribute("book", booksService.findOne(id));
//        Optional<Person> bookOwner = bookDAO.getBookOwner(id);
        Optional<Person> bookOwner = Optional.ofNullable(booksService.findByIdFetchBook(id));

//        model.addAttribute("people", peopleService.findAll());
        if (bookOwner.isPresent())
            model.addAttribute("owner", bookOwner.get());
        else
            model.addAttribute("people", peopleService.findAll());

        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book Book) {
        return "books/new";
    }

    @GetMapping("/search")
    public String search(Model model,
                         @RequestParam(value = "title", required = false) String title) {
//        model.addAttribute("book", booksService.findByName(title));
        if (title != null && booksService.findByName(title).size() > 0) {
            Book book = booksService.findByName(title).get(0);
            model.addAttribute("book", book);
            Optional<Person> bookOwner = Optional.ofNullable(booksService.findByIdFetchBook(book.getId()));

//        model.addAttribute("people", peopleService.findAll());
            if (bookOwner.isPresent()) {
                model.addAttribute("owner", bookOwner.get());
            } else {
                model.addAttribute("message", "Книга свободна");
            }
        }
        return "/books/search";
    }


    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book Book,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "books/new";

        booksService.save(Book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", booksService.findOne(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "books/edit";

        booksService.update(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        booksService.delete(id);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        booksService.release(id);
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson) {
        booksService.assign(id, selectedPerson);
        return "redirect:/books/" + id;
    }
}
