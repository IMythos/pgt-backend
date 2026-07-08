package com.portable.microservices.ms_inventory.kardex.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.portable.microservices.ms_inventory.kardex.domain.model.Kardex;
import com.portable.microservices.ms_inventory.kardex.domain.ports.out.KardexPersistencePortOut;

@ExtendWith(MockitoExtension.class)
class FindKardexUseCaseTest {
    @Mock
    private KardexPersistencePortOut kardexPersistence;

    @InjectMocks
    private FindKardexUseCase useCase;

    @Test
    void findAllWithFiltersDebeNormalizarTextoNulo() {
        when(kardexPersistence.findAllWithFilters(null, null, null, "", 0, 10)).thenReturn(List.of());
        when(kardexPersistence.countAllWithFilters(null, null, null, "")).thenReturn(0L);

        var response = useCase.findAllWithFilters(null, null, null, null, 0, 10);

        assertEquals(0, response.total());
        verify(kardexPersistence).findAllWithFilters(null, null, null, "", 0, 10);
        verify(kardexPersistence).countAllWithFilters(null, null, null, "");
    }

    @Test
    void findByProductIdPaginadoDebeUsarItemsYTotalDelPuerto() {
        UUID productId = UUID.randomUUID();
        List<Kardex> items = List.of();
        when(kardexPersistence.findByProductId(productId, 1, 20)).thenReturn(items);
        when(kardexPersistence.countByProductId(productId)).thenReturn(3L);

        var response = useCase.findByProductId(productId, 1, 20);

        assertEquals(3L, response.total());
        assertEquals(1, response.page());
        assertEquals(20, response.pageSize());
    }
}
