package guru.springframework.springrestclientexamples;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jt on 9/22/17.
 */
public class RestTemplateExamples {

    public static final String API_ROOT = "https://api.predic8.de/shop/v2";

    @Test
    public void getProducts() throws Exception {
        String apiUrl = API_ROOT + "/products/";

        RestTemplate restTemplate = new RestTemplate();

        JsonNode jsonNode = restTemplate.getForObject(apiUrl, JsonNode.class);//response in jsonNode format

        System.out.println("Response");
        System.out.println(jsonNode.toString());
    }

    @Test
    public void getCustomers() throws Exception {
        String apiUrl = API_ROOT + "/customers/";

        RestTemplate restTemplate = new RestTemplate();

        JsonNode jsonNode = restTemplate.getForObject(apiUrl, JsonNode.class);

        System.out.println("Response");
        System.out.println(jsonNode.toString());
    }

    @Test
    public void createProduct() throws Exception {
        String apiUrl = API_ROOT + "/products/";

        RestTemplate restTemplate = new RestTemplate();

        //Java object to parse to JSON
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("name", "Joe");
        postMap.put("price", 2.79);

        JsonNode jsonNode = restTemplate.postForObject(apiUrl, postMap, JsonNode.class);

        System.out.println("Response");
        System.out.println(jsonNode.toString());
    }

    @Test
    public void updateVendor() throws Exception {//error resolved after using apache http
        //create customer to update
        String apiUrl = API_ROOT + "/vendors/";
        //RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();//apache http
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        //Java object to parse to JSON
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("name", "hello");
        //postMap.put("price", 2.78);
        JsonNode jsonNode = restTemplate.postForObject(apiUrl, postMap, JsonNode.class);

        System.out.println("Response");
        System.out.println(jsonNode.toString());
        String customerUrl = jsonNode.get("self_link").textValue();

        String id = customerUrl.split("/")[4];
        System.out.println("Created customer id: " + id);

        //Map<String, Object> postMap1 = new HashMap<>();
        postMap.put("name", "alalla");
        //postMap.put("price", 2.7977);
        restTemplate.put(apiUrl + id, postMap);

        JsonNode updatedNode = restTemplate.getForObject(apiUrl + id, JsonNode.class);
        System.out.println(updatedNode.toString());

    }

    @Test(expected = ResourceAccessException.class)
    public void updateCustomerUsingPatchSunHttp() throws Exception {//to be failde bcz sun dont support patch

        //create customer to update
        String apiUrl = API_ROOT + "/products/";

        RestTemplate restTemplate = new RestTemplate();

        //Java object to parse to JSON
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("firstname", "Sam");
        postMap.put("lastname", "Axe");

        JsonNode jsonNode = restTemplate.postForObject(apiUrl, postMap, JsonNode.class);

        System.out.println("Response");
        System.out.println(jsonNode.toString());

        String customerUrl = jsonNode.get("customer_url").textValue();

        String id = customerUrl.split("/")[3];

        System.out.println("Created customer id: " + id);

        postMap.put("firstname", "Sam 2");
        postMap.put("lastname", "Axe 2");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(postMap, headers);

        //fails due to sun.net.www.protocol.http.HttpURLConnection not supporting patch
        JsonNode updatedNode = restTemplate.patchForObject(apiUrl + id, entity, JsonNode.class);

        System.out.println(updatedNode.toString());

    }

    @Test
    public void updateProductsUsingPatch() throws Exception {

        //create customer to update
        String apiUrl = API_ROOT + "/products/";

        // Use Apache HTTP client factory
        //see: https://github.com/spring-cloud/spring-cloud-netflix/issues/1777
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        //Java object to parse to JSON
        Map<String, Object> postMap = new HashMap<>();

        postMap.put("name", "yoyo");;
        postMap.put("price", 11.11);
        //postMap.put("lastname", "Axe");

        JsonNode jsonNode = restTemplate.postForObject(apiUrl, postMap, JsonNode.class);

        System.out.println("Response");
        System.out.println(jsonNode.toString());

        String customerUrl = jsonNode.get("self_link").textValue();

        String id = customerUrl.split("/")[4];

        System.out.println("Created customer id: " + id);

        postMap.put("price", 22.11);
        //postMap.put("lastname", "Axe 2");

        //example of setting headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(postMap, headers);

        JsonNode updatedNode = restTemplate.patchForObject(apiUrl + id, entity, JsonNode.class);

        System.out.println(updatedNode.toString());
    }

    @Test(expected = HttpClientErrorException.class)
    public void deleteCustomer() throws Exception {

        //create customer to update
        String apiUrl = API_ROOT + "/customers/";

        RestTemplate restTemplate = new RestTemplate();

        //Java object to parse to JSON
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("firstname", "Les");
        postMap.put("lastname", "Claypool");

        JsonNode jsonNode = restTemplate.postForObject(apiUrl, postMap, JsonNode.class);

        System.out.println("Response");
        System.out.println(jsonNode.toString());

        String customerUrl = jsonNode.get("customer_url").textValue();

        String id = customerUrl.split("/")[3];

        System.out.println("Created customer id: " + id);

        restTemplate.delete(apiUrl + id); //expects 200 status

        System.out.println("Customer deleted");

        //should go boom on 404
        restTemplate.getForObject(apiUrl + id, JsonNode.class);

    }


}