package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.AuthorizeTransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class AuthorizationService {

    @Autowired
    private RestTemplate restTemplate;

    public boolean authorizeTransaction(User sender, BigDecimal value) {
        ResponseEntity<AuthorizeTransactionDto> authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", AuthorizeTransactionDto.class);
        return authorizationResponse.getStatusCode() == HttpStatus.OK
                && Objects.requireNonNull(authorizationResponse.getBody()).data().authorization();
    }
}
