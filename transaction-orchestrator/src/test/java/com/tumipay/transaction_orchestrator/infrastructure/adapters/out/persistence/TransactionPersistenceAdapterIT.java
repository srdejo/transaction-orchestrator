package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence;

import com.tumipay.transaction_orchestrator.BaseIntegrationTest;
import com.tumipay.transaction_orchestrator.TestFixtures;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.model.TransactionStatus;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.adapter.TransactionPersistenceAdapter;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.mapper.CustomerEntityMapper;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.mapper.TransactionEntityMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TransactionPersistenceAdapter.class, TransactionEntityMapper.class, CustomerEntityMapper.class})
@DisplayName("TransactionPersistenceAdapter Integration Tests")
class TransactionPersistenceAdapterIT extends BaseIntegrationTest {

    @Autowired
    private TransactionPersistenceAdapter adapter;

    @Test
    @DisplayName("Given a valid domain Transaction, when save, then is persisted and can be retrieved")
    void givenValidTransaction_whenSave_thenIsPersisted() {
        // Arrange: Use a Payment Method UUID from seed (e.g., CARD = 550e8400-e29b-41d4-a716-446655440000)
        String pmId = "550e8400-e29b-41d4-a716-446655440001";
        Transaction transaction = TestFixtures.buildTransaction(UUID.randomUUID().toString(), 15000, TransactionStatus.PENDING);
        
        // Re-build with seeded PM ID to avoid FK violation
        transaction = Transaction.reconstruct(
            transaction.getId(),
            transaction.getClientTransactionId(),
            transaction.getAmount(),
            transaction.getCountryCode(),
            new com.tumipay.transaction_orchestrator.domain.model.PaymentMethod(pmId),
            transaction.getWebhookUrl(),
            transaction.getRedirectUrl(),
            transaction.getCustomer(),
            transaction.getDescription(),
            null,
            transaction.getStatus(),
            transaction.getCreatedAt()
        );

        // Act
        Transaction saved = adapter.save(transaction);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(transaction.getId());
        
        Optional<Transaction> found = adapter.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getClientTransactionId()).isEqualTo(transaction.getClientTransactionId());
        assertThat(found.get().getAmount().amount().longValue()).isEqualTo(15000);
    }

    @Test
    @DisplayName("Given an existing transaction, when update status, then changes are persisted")
    void givenExistingTransaction_whenUpdateStatus_thenIsPersisted() {
        // Arrange
        String pmId = "550e8400-e29b-41d4-a716-446655440001";
        Transaction transaction = TestFixtures.buildTransaction(UUID.randomUUID().toString(), 20000, TransactionStatus.PENDING);
        transaction = Transaction.reconstruct(
            transaction.getId(), "UPDATE-TEST-001", transaction.getAmount(),
            transaction.getCountryCode(), new com.tumipay.transaction_orchestrator.domain.model.PaymentMethod(pmId),
            transaction.getWebhookUrl(), transaction.getRedirectUrl(), transaction.getCustomer(),
            transaction.getDescription(), null, TransactionStatus.PENDING, transaction.getCreatedAt()
        );
        adapter.save(transaction);

        // Act
        transaction.process();
        adapter.save(transaction);

        // Assert
        Optional<Transaction> found = adapter.findById(transaction.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(TransactionStatus.PROCESSING);
    }
}
