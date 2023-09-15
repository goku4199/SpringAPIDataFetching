package guru.springframework.springrestclientexamples.services;

import guru.springframework.api.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ApiService {

    //traditional way
    List<User> getUsers(Integer limit);

    //reactive way
    Flux getUsers1(Mono<Integer> limit);
}
