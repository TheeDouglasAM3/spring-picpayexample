package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.NotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {
    @Autowired
    private RestTemplate restTemplate;

    public void sendNotification(User user, String message) {
        String email = user.getEmail();
        var notificationRequest = new NotificationDto(email, message);
        RestTemplate restTemplate = new RestTemplate();


        ResponseEntity<Void> response = restTemplate.postForEntity(
                "https://util.devi.tools/api/v1/notify",
                notificationRequest,
                Void.class
        );

        if(!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Erro ao enviar a notificação");
            // throw new Exception("Serviço de notificação está fora do ar");
        }

    }
}
