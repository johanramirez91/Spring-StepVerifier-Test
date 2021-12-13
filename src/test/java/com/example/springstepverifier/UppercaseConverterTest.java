package com.example.springstepverifier;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;


/*
En ocasiones es posible que necesitemos datos especiales para activar ciertos comportamientos concretos
que se quieran probar. TestPublisher<T> permite activar señales de prueba como si de un publicador real se tratara. Métodos más comunes:
* next(T value) o next(T value, T rest) - envía una o más señales a los suscriptores
* emit(T value) - igual que next(T) pero invoca complete() al finalizar
* complete() - termina la fuente con la señal completar
* error(Throwable tr) - termina una fuente con un error
* flux() - método para encolver un TestPublisher en Flux
* mono() - lo mismo que flux() pero envolviéndolo en Mono
*/

@SpringBootTest
public class UppercaseConverterTest {

    final TestPublisher<String> testPublisher = TestPublisher.create();
    @Test
    void testUpperCase(){
        UppercaseConverter uppercaseConverter = new UppercaseConverter(testPublisher.flux());
        StepVerifier.create(uppercaseConverter.getUpperCase())
                .then(() -> testPublisher.emit("datos", "GeNeRaDoS", "Sofka"))
                .expectNext("DATOS", "GENERADOS", "SOFKA")
                .verifyComplete();
    }
}
