package guru.springframework.springrestclientexamples.services;

import guru.springframework.api.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ApiServiceImpl implements ApiService{

    private RestTemplate restTemplate;
    private final String api_url;

    public ApiServiceImpl(RestTemplate restTemplate, @Value("${api.url}") String api_url) {//Added value of spring not lombok value is adding api_url from application.properties
        this.restTemplate = restTemplate;
        this.api_url = api_url;

    }

    //Traditional way
    @Override
    public List<User> getUsers(Integer limit) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(api_url).queryParam("limit",limit);
        //UriComponentBulider is just another way to build uris...queryparam will put a query param limit

        //List<User> userData = restTemplate.getForObject("http://apifaketory.com/api/user?limit=" + limit, List.class);

        List<User> userData = restTemplate.getForObject(uriBuilder.toUriString(), List.class);//toUriString converts to string
        return userData;
    }

    //Reactive way
    public Flux getUsers1(Mono<Integer> limit){

        return WebClient.create(api_url)//creating the url
                .get()//creating the url
                .uri(uriBuilder -> uriBuilder.queryParam("limit",limit.block()).build())//limit coming as mono so using .block extracting it from mono
                .accept(MediaType.APPLICATION_JSON)//accepting json
                .exchange()//exchange to get response
                .flatMap(resp -> resp.bodyToMono(List.class))//converting response to mono
                .flatMapIterable(list -> list);//converting mono to flux

        

    }
    //note this function getUsers1 was edited since the new api dont have UserData. If it had UserData class
    //then .flatMap(resp -> resp.bodyToMono(UserData.class))
    //     .flatMapIterable(UserData::getData());


}
