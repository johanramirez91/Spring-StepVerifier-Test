package com.example.springstepverifier;

import com.example.springstepverifier.services.Servicio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
public class ServiceTest {

    @Autowired
    Servicio servicio;

    @Test
    void testMono(){
        Mono<String> uno = servicio.buscarUno();
        StepVerifier.create(uno).expectNext("Pedro").verifyComplete();
    }

    @Test
    void testVarios(){
        Flux<String> uno = servicio.buscarTodos();
        StepVerifier.create(uno)
                .expectNext("Pedro")
                .expectNext("Maria")
                .expectNext("Jesus")
                .expectNext("Carmen")
                .verifyComplete();
    }

    @Test
    void testVariosLento() {
        Flux<String> uno = servicio.buscarTodosLento();
        StepVerifier.create(uno)
                .expectNext("Pedro")
                .thenAwait(Duration.ofSeconds(1))
                .expectNext("Maria")
                .thenAwait(Duration.ofSeconds(1))
                .expectNext("Jesus")
                .thenAwait(Duration.ofSeconds(1))
                .expectNext("Carmen")
                .thenAwait(Duration.ofSeconds(1)).verifyComplete();
    }

    @Test
    void testTodosFiltro1() {
        Flux<String> source = servicio.buscarTodosFiltro();
        StepVerifier
                .create(source)
                .expectNext("JOHN")
                .expectNextMatches(name -> name.startsWith("MA"))
                .expectNext("CLOE", "CATE")
                .expectComplete()
                .verify();
    }

    @Test
    void testTodosFiltro2(){
        Flux<String> source = servicio.buscarTodosFiltro();
        StepVerifier
                .create(source)
                .expectNextCount(4)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Mensaje de Error")
                ).verify();
    }

    @Test
    void testVirtualTime(){
        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofSeconds(1)).take(2))
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(1)) // falla cuando aparece algún evento durante la duración
                .expectNext(0L)
                .thenAwait(Duration.ofSeconds(1)) // detiene la evaluación, pueden ocurrir nuevos eventos
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void testPause() {
        Flux<Integer> source = Flux.<Integer>create(emmiter -> {
            emmiter.next(1);
            emmiter.next(2);
            emmiter.next(3);
            emmiter.next(4);
        }).filter(number -> number % 2 == 0);

        StepVerifier.create(source)
                .expectNext(2)
                .expectComplete()
                .verifyThenAssertThat()
                .hasDropped(4)
                .tookLessThan(Duration.ofMillis(1050));
    }
}
