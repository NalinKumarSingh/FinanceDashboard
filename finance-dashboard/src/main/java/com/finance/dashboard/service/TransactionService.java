package com.finance.dashboard.service;

import com.finance.dashboard.dto.request.TransactionRequest;
import com.finance.dashboard.dto.response.TransactionResponse;
import com.finance.dashboard.entity.Transaction;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.Role;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.exception.UnauthorizedActionException;
import com.finance.dashboard.repository.TransactionRepository;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionResponse createTransaction(TransactionRequest request, String email) {
        User user = findUserByEmailOrThrow(email);
        Transaction transaction = Transaction.builder()
                .userId(user.getId())
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .build();
        return new TransactionResponse(transactionRepository.save(transaction));
    }

    public List<TransactionResponse> getTransactions(
            TransactionType type, String category, LocalDate from, LocalDate to) {
        return transactionRepository.findAllWithFilters(type, category, from, to)
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }

    public TransactionResponse getTransactionById(Long id) {
        return new TransactionResponse(findTransactionOrThrow(id));
    }

    public TransactionResponse updateTransaction(Long id, TransactionRequest request, String email) {
        Transaction transaction = findTransactionOrThrow(id);
        User user = findUserByEmailOrThrow(email);
        if (user.getRole() != Role.ADMIN && !transaction.getUserId().equals(user.getId())) {
            throw new UnauthorizedActionException("You can only update your own transactions");
        }
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setNotes(request.getNotes());
        return new TransactionResponse(transactionRepository.save(transaction));
    }

    public void deleteTransaction(Long id, String email) {
        Transaction transaction = findTransactionOrThrow(id);
        User user = findUserByEmailOrThrow(email);
        if (user.getRole() != Role.ADMIN && !transaction.getUserId().equals(user.getId())) {
            throw new UnauthorizedActionException("You can only delete your own transactions");
        }
        transaction.setIsDeleted(true);
        transactionRepository.save(transaction);
    }

    private Transaction findTransactionOrThrow(Long id) {
        return transactionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    private User findUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
