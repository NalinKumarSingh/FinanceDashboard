package com.finance.dashboard.repository;

import com.finance.dashboard.entity.Transaction;
import com.finance.dashboard.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdAndIsDeletedFalse(Long id);

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.isDeleted = false
          AND (:type IS NULL OR t.type = :type)
          AND (:category IS NULL OR LOWER(t.category) = LOWER(:category))
          AND (:from IS NULL OR t.date >= :from)
          AND (:to IS NULL OR t.date <= :to)
        ORDER BY t.date DESC
    """)
    List<Transaction> findAllWithFilters(
            @Param("type") TransactionType type,
            @Param("category") String category,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = :type AND t.isDeleted = false")
    BigDecimal sumByType(@Param("type") TransactionType type);

    @Query("""
        SELECT t.category, COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.isDeleted = false
        GROUP BY t.category
    """)
    List<Object[]> sumByCategory();

    @Query("""
        SELECT MONTH(t.date), YEAR(t.date), t.type, COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.isDeleted = false
          AND t.date >= :from
        GROUP BY YEAR(t.date), MONTH(t.date), t.type
        ORDER BY YEAR(t.date), MONTH(t.date)
    """)
    List<Object[]> monthlyTrends(@Param("from") LocalDate from);

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.isDeleted = false
        ORDER BY t.createdAt DESC
        LIMIT 10
    """)
    List<Transaction> findRecentActivity();
}
