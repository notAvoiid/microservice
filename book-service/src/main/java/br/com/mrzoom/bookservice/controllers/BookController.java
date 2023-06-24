package br.com.mrzoom.bookservice.controllers;

import br.com.mrzoom.bookservice.models.Book;
import br.com.mrzoom.bookservice.proxy.CambioProxy;
import br.com.mrzoom.bookservice.repositories.BookRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Tag(name = "Book endpoint")
@RestController
@RequestMapping("/book-service")
public class BookController {

    @Autowired
    private Environment environment;

    @Autowired
    private BookRepository repository;

    @Autowired
    private CambioProxy proxy;

    // http://localhost:8100/book-service/1/BRL
    @Operation(summary = "Find a specific book by your ID")
    @GetMapping(value = "{id}/{currency}")
    public Book findBook(@PathVariable("id")Long id, @PathVariable("currency")String currency){

        var book = repository.getReferenceById(id);

        HashMap<String, String> params = new HashMap<>();
        params.put("amount", book.getPrice().toString());
        params.put("from", "USD");
        params.put("to", currency);

        var cambio = proxy.getCambio(book.getPrice(), "USD", currency);

        var port = environment.getProperty("local.server.port");
        book.setEnvironment("Book port: " + port + " Cambio port:" + cambio.getEnvironment());
        book.setPrice(cambio.getConvertedValue());

        return book;
    }

}
