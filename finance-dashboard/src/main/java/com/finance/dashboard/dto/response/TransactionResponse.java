package com.finance.dashboard.dto.response;

import com.finance.dashboard.entity.Transaction;
import com.finance.dashboard.enums.TransactionType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class TransactionResponse {

    private final Long id;
    private final Long userId;
    private final BigDecimal amount;
    private final TransactionType type;
    private final String category;
    private final LocalDate date;
    private final String notes;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public TransactionResponse(Transaction t) {
        this.id = t.getId();
        this.userId = t.getUserId();
        this.amount = t.getAmount();
        this.type = t.getType();
        this.category = t.getCategory();
        this.date = t.getDate();
        this.notes = t.getNotes();
        this.createdAt = t.getCreatedAt();
        this.updatedAt = t.getUpdatedAt();
    }
}
