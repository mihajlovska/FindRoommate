package com.roommate.elasticsearch.controller;

import com.roommate.api.model.AppUser;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/users")
public class UserController {

    @Autowired
    Client client;
    @PostMapping("/create")
    public String create(@RequestBody AppUser user) throws IOException {

        IndexResponse response = client.prepareIndex("users", "roommates", user.getUserId().toString())
                .setSource(jsonBuilder()
                        .startObject()
                        .field("FirstName", user.getFirstName())
                        .field("LastName", user.getLastName())
                        .endObject()
                )
                .get();
        System.out.println("response id:"+response.getId());
        return response.getResult().toString();
    }


    @GetMapping("/view/{id}")
    public Map<String, Object> view(@PathVariable final String id) {
        GetResponse getResponse = client.prepareGet("users", "roommates", id).get();
        System.out.println(getResponse.getSource());


        return getResponse.getSource();
    }
    @GetMapping("/view/name/{field}")
    public List<SearchHit> searchByName(@PathVariable final String field) {
        Map<String,Object> map = null;
        SearchResponse response = client.prepareSearch("users")
                .setTypes("roommates")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchQuery("FirstName", field))
                .get()
                ;
        List<SearchHit> test = Arrays.asList(response.getHits().getHits());
        return Arrays.asList(response.getHits().getHits());

    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable final String id) throws IOException {

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("users")
                .type("roommates")
                .id(id)
                .doc(jsonBuilder()
                        .startObject()
                        .field("FirstName", "Stefanija")
                        .endObject());
        try {
            UpdateResponse updateResponse = client.update(updateRequest).get();
            System.out.println(updateResponse.status());
            return updateResponse.status().toString();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e);
        }
        return "Exception";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable final String id) {

        DeleteResponse deleteResponse = client.prepareDelete("users", "roommates", id).get();

        System.out.println(deleteResponse.getResult().toString());
        return deleteResponse.getResult().toString();
    }

}