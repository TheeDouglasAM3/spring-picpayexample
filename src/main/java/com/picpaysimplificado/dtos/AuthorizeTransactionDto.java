package com.picpaysimplificado.dtos;

public record AuthorizeTransactionDto(String status, Data data) {
    public static record Data(boolean authorization) {}
}
