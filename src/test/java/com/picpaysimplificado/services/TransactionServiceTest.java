package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.TransactionDto;
import com.picpaysimplificado.repositories.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuthorizationService authService;

    @Mock
    private TransactionRepository repository;

    @Autowired
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should create transaction successfully when everything is OK")
    void createTransactionCase1() throws Exception {
        var sender = new User(1L, "Maria", "Souza", "99999999901",
                "maria@gmail.com", "12345", new BigDecimal(1000), UserType.COMMON);

        var receiver = new User(2L, "João", "Souza", "99999999902",
                "joao@gmail.com", "12345", new BigDecimal(1000), UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authService.authorizeTransaction(any(), any())).thenReturn(true);

        var request = new TransactionDto(new BigDecimal(100), 1L, 2L);
        transactionService.createTransaction(request);

        verify(repository, times(1)).save(any());

        sender.setBalance(new BigDecimal(900));
        verify(userService, times(1)).saveUser(sender);

        receiver.setBalance(new BigDecimal(1100));
        verify(userService, times(1)).saveUser(receiver);

        verify(notificationService, times(1)).sendNotification(sender, "Transação realizada com sucesso");
        verify(notificationService, times(1)).sendNotification(receiver, "Transação recebida com sucesso");
    }

    @Test
    @DisplayName("Should throw Exception when transaction is not allowed")
    void createTransactionCase2() throws Exception {
        var sender = new User(1L, "Maria", "Souza", "99999999901",
                "maria@gmail.com", "12345", new BigDecimal(1000), UserType.COMMON);

        var receiver = new User(2L, "João", "Souza", "99999999902",
                "joao@gmail.com", "12345", new BigDecimal(1000), UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authService.authorizeTransaction(any(), any())).thenReturn(false);

        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            var request = new TransactionDto(new BigDecimal(100), 1L, 2L);
            transactionService.createTransaction(request);
        });

        Assertions.assertEquals("Transação não autorizada", thrown.getMessage());
    }
}