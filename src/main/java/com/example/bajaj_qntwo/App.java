package com.example.bajaj_qntwo;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class App {

    private final RestTemplate restTemplate = new RestTemplate();

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        try {
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", "John Doe");
            requestBody.put("regNo", "REG12347");
            requestBody.put("email", "john@example.com");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            System.out.println("Response: " + response.getBody());

            String webhookUrl = response.getBody().get("webhook").toString();
            String accessToken = response.getBody().get("accessToken").toString();

            
            String finalQuery = "select d.DEPARTMENT_NAME, AVG(TIMESTAMPDIFF(YEAR, e.DOB, CURDATE())) as AVERAGE_AGE, GROUP_CONCAT(CONCAT( e.FIRST_NAME, ' ', e.LAST_NAME) order by e.EMP_ID separator ', ' LIMIT 10) as EMPLOYEE_LIST from DEPARTMENT d join EMPLOYEE e on d.DEPARTMENT_ID = e.DEPARTMENT join PAYMENTS p on e.EMP_ID = p.EMP_ID where p.AMOUNT > 70000 group by d.DEPARTMENT_ID, d.DEPARTMENT_NAME order by d.DEPARTMENT_ID desc";

            submitAnswer(webhookUrl, accessToken, finalQuery);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void submitAnswer(String webhookUrl, String token, String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        Map<String, String> body = new HashMap<>();
        body.put("finalQuery", query);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, entity, String.class);

        System.out.println("Final submission response: " + response.getBody());
    }
}
