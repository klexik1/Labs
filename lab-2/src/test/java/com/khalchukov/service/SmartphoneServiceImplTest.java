package com.khalchukov.service;

import com.khalchukov.dao.SmartphoneRepository;
import com.khalchukov.entity.SmartphoneEntity;
import com.khalchukov.exception.SmartphoneNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmartphoneServiceImplTest {

    @Mock
    private SmartphoneRepository smartphoneRepository;

    @InjectMocks
    private SmartphoneServiceImpl smartphoneService;

    //  Тесты для findById

    @Test
    void findById_shouldReturnSmartphone_whenExists() {
        SmartphoneEntity smartphone = new SmartphoneEntity(1, "Samsung Galaxy S24", 79999.99);
        when(smartphoneRepository.findById(1)).thenReturn(smartphone);

        SmartphoneEntity result = smartphoneService.findById(1);

        assertEquals(smartphone, result);
        assertEquals(1, result.getId());
        assertEquals("Samsung Galaxy S24", result.getModel());
        assertEquals(79999.99, result.getPrice());
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        when(smartphoneRepository.findById(99)).thenReturn(null);

        SmartphoneNotFoundException exception = assertThrows(
                SmartphoneNotFoundException.class,
                () -> smartphoneService.findById(99)
        );

        assertTrue(exception.getMessage().contains("99"));
    }

    @Test
    void findById_shouldThrowException_whenNotFound_withNegativeId() {
        when(smartphoneRepository.findById(-1)).thenReturn(null);

        assertThrows(SmartphoneNotFoundException.class, () -> smartphoneService.findById(-1));
    }

    @Test
    void findById_shouldThrowException_whenNotFound_withZeroId() {
        when(smartphoneRepository.findById(0)).thenReturn(null);

        assertThrows(SmartphoneNotFoundException.class, () -> smartphoneService.findById(0));
    }

    //  Тесты для save

    @Test
    void save_shouldReturnId_whenSaved() {
        SmartphoneEntity smartphone = new SmartphoneEntity("Apple iPhone 16", 99999.99);
        when(smartphoneRepository.save(any(SmartphoneEntity.class))).thenReturn(5);

        int id = smartphoneService.save("Apple iPhone 16", 99999.99);

        assertEquals(5, id);
        verify(smartphoneRepository).save(argThat(entity ->
                "Apple iPhone 16".equals(entity.getModel()) &&
                        entity.getPrice() == 99999.99
        ));
    }

    @Test
    void save_shouldReturnPositiveId() {
        when(smartphoneRepository.save(any(SmartphoneEntity.class))).thenReturn(1);

        int id = smartphoneService.save("Test Model", 1000.0);

        assertTrue(id > 0);
    }

    @Test
    void save_shouldWorkWithMinimumPrice() {
        when(smartphoneRepository.save(any(SmartphoneEntity.class))).thenReturn(1);

        int id = smartphoneService.save("Budget Phone", 0.01);

        assertEquals(1, id);
    }

    @Test
    void save_shouldWorkWithHighPrice() {
        when(smartphoneRepository.save(any(SmartphoneEntity.class))).thenReturn(1);

        int id = smartphoneService.save("Premium Phone", 999999.99);

        assertEquals(1, id);
    }

    @Test
    void save_shouldWorkWithSpecialCharactersInModel() {
        when(smartphoneRepository.save(any(SmartphoneEntity.class))).thenReturn(1);

        int id = smartphoneService.save("Phone & Model (2024)", 50000.0);

        assertEquals(1, id);
    }

    @Test
    void save_shouldWorkWithUnicodeCharactersInModel() {
        when(smartphoneRepository.save(any(SmartphoneEntity.class))).thenReturn(1);

        int id = smartphoneService.save("Смартфон 中文", 30000.0);

        assertEquals(1, id);
    }

    @Test
    void save_shouldWorkWithEmptyModel() {
        when(smartphoneRepository.save(any(SmartphoneEntity.class))).thenReturn(1);

        int id = smartphoneService.save("", 1000.0);

        assertEquals(1, id);
    }

    @Test
    void save_shouldWorkWithWhitespaceModel() {
        when(smartphoneRepository.save(any(SmartphoneEntity.class))).thenReturn(1);

        int id = smartphoneService.save("   ", 1000.0);

        assertEquals(1, id);
    }

    @Test
    void save_shouldWorkWithVeryLongModelName() {
        String longName = "A".repeat(1000);
        when(smartphoneRepository.save(any(SmartphoneEntity.class))).thenReturn(1);

        int id = smartphoneService.save(longName, 1000.0);

        assertEquals(1, id);
    }

    //  Тесты для update

    @Test
    void update_shouldThrowException_whenNotFound() {
        SmartphoneEntity smartphone = new SmartphoneEntity(99, "Нет такого", 1.0);
        when(smartphoneRepository.update(smartphone)).thenReturn(false);

        SmartphoneNotFoundException exception = assertThrows(
                SmartphoneNotFoundException.class,
                () -> smartphoneService.update(smartphone)
        );

        assertTrue(exception.getMessage().contains("99"));
    }

    @Test
    void update_shouldNotThrow_whenExists() {
        SmartphoneEntity smartphone = new SmartphoneEntity(1, "Samsung Galaxy S24 Ultra", 89999.99);
        when(smartphoneRepository.update(smartphone)).thenReturn(true);

        assertDoesNotThrow(() -> smartphoneService.update(smartphone));
        verify(smartphoneRepository).update(smartphone);
    }

    @Test
    void update_shouldWorkWithNegativeId() {
        SmartphoneEntity smartphone = new SmartphoneEntity(-1, "Test", 100.0);
        when(smartphoneRepository.update(smartphone)).thenReturn(false);

        assertThrows(SmartphoneNotFoundException.class, () -> smartphoneService.update(smartphone));
    }

    @Test
    void update_shouldWorkWithZeroId() {
        SmartphoneEntity smartphone = new SmartphoneEntity(0, "Test", 100.0);
        when(smartphoneRepository.update(smartphone)).thenReturn(false);

        assertThrows(SmartphoneNotFoundException.class, () -> smartphoneService.update(smartphone));
    }

    @Test
    void update_shouldWorkWithUpdatedModel() {
        SmartphoneEntity smartphone = new SmartphoneEntity(1, "Old Model", 50000.0);
        when(smartphoneRepository.update(smartphone)).thenReturn(true);

        assertDoesNotThrow(() -> smartphoneService.update(smartphone));
    }

    @Test
    void update_shouldWorkWithUpdatedPrice() {
        SmartphoneEntity smartphone = new SmartphoneEntity(1, "Test Model", 999999.99);
        when(smartphoneRepository.update(smartphone)).thenReturn(true);

        assertDoesNotThrow(() -> smartphoneService.update(smartphone));
    }

    //  Тесты для findByModel

    @Test
    void findByModel_shouldReturnSmartphone_whenExists() {
        SmartphoneEntity smartphone = new SmartphoneEntity(2, "Google Pixel 9", 69999.99);
        when(smartphoneRepository.findAll()).thenReturn(List.of(smartphone));

        SmartphoneEntity result = smartphoneService.findByModel("Google Pixel 9");

        assertEquals(smartphone, result);
        assertEquals("Google Pixel 9", result.getModel());
    }

    @Test
    void findByModel_shouldReturnFirstMatch_whenMultipleExist() {
        List<SmartphoneEntity> smartphones = List.of(
                new SmartphoneEntity(1, "Samsung Galaxy S24", 79999.99),
                new SmartphoneEntity(2, "Google Pixel 9", 69999.99)
        );
        when(smartphoneRepository.findAll()).thenReturn(smartphones);

        SmartphoneEntity result = smartphoneService.findByModel("Google Pixel 9");

        assertEquals("Google Pixel 9", result.getModel());
    }

    @Test
    void findByModel_shouldThrowException_whenNotFound() {
        when(smartphoneRepository.findAll()).thenReturn(List.of());

        SmartphoneNotFoundException exception = assertThrows(
                SmartphoneNotFoundException.class,
                () -> smartphoneService.findByModel("Несуществующий")
        );

        assertTrue(exception.getMessage().contains("Несуществующий"));
    }

    @Test
    void findByModel_shouldThrowException_whenEmptyModel() {
        when(smartphoneRepository.findAll()).thenReturn(List.of());

        assertThrows(SmartphoneNotFoundException.class, () -> smartphoneService.findByModel(""));
    }

    @Test
    void findByModel_shouldThrowException_whenNullModel() {
        when(smartphoneRepository.findAll()).thenReturn(List.of());

        assertThrows(SmartphoneNotFoundException.class, () -> smartphoneService.findByModel(null));
    }

    @Test
    void findByModel_shouldBeCaseSensitive() {
        SmartphoneEntity smartphone = new SmartphoneEntity(1, "Samsung Galaxy S24", 79999.99);
        when(smartphoneRepository.findAll()).thenReturn(List.of(smartphone));

        SmartphoneNotFoundException exception = assertThrows(
                SmartphoneNotFoundException.class,
                () -> smartphoneService.findByModel("samsung galaxy s24")
        );

        assertTrue(exception.getMessage().contains("samsung galaxy s24"));
    }

    @Test
    void findByModel_shouldWorkWithSpecialCharacters() {
        SmartphoneEntity smartphone = new SmartphoneEntity(1, "Phone & Model (2024)", 50000.0);
        when(smartphoneRepository.findAll()).thenReturn(List.of(smartphone));

        SmartphoneEntity result = smartphoneService.findByModel("Phone & Model (2024)");

        assertNotNull(result);
        assertEquals("Phone & Model (2024)", result.getModel());
    }

    @Test
    void findByModel_shouldWorkWithUnicode() {
        SmartphoneEntity smartphone = new SmartphoneEntity(1, "Смартфон 中文", 30000.0);
        when(smartphoneRepository.findAll()).thenReturn(List.of(smartphone));

        SmartphoneEntity result = smartphoneService.findByModel("Смартфон 中文");

        assertNotNull(result);
        assertEquals("Смартфон 中文", result.getModel());
    }

    //  Тесты для deleteById

    @Test
    void deleteById_shouldCallRepository() {
        smartphoneService.deleteById(1);

        verify(smartphoneRepository).deleteById(1);
    }

    @Test
    void deleteById_shouldWorkWithNegativeId() {
        smartphoneService.deleteById(-1);

        verify(smartphoneRepository).deleteById(-1);
    }

    @Test
    void deleteById_shouldWorkWithZeroId() {
        smartphoneService.deleteById(0);

        verify(smartphoneRepository).deleteById(0);
    }

    @Test
    void deleteById_shouldWorkWithLargeId() {
        smartphoneService.deleteById(Integer.MAX_VALUE);

        verify(smartphoneRepository).deleteById(Integer.MAX_VALUE);
    }

    //  Тесты для findAll

    @Test
    void findAll_shouldReturnAllSmartphones() {
        List<SmartphoneEntity> smartphones = List.of(
                new SmartphoneEntity(1, "Samsung Galaxy S24", 79999.99),
                new SmartphoneEntity(2, "Apple iPhone 16", 99999.99)
        );
        when(smartphoneRepository.findAll()).thenReturn(smartphones);

        List<SmartphoneEntity> result = smartphoneService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoSmartphones() {
        when(smartphoneRepository.findAll()).thenReturn(List.of());

        List<SmartphoneEntity> result = smartphoneService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldReturnSingleSmartphone() {
        List<SmartphoneEntity> smartphones = List.of(
                new SmartphoneEntity(1, "Samsung Galaxy S24", 79999.99)
        );
        when(smartphoneRepository.findAll()).thenReturn(smartphones);

        List<SmartphoneEntity> result = smartphoneService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findAll_shouldReturnSmartphonesInOrder() {
        List<SmartphoneEntity> smartphones = List.of(
                new SmartphoneEntity(1, "Samsung Galaxy S24", 79999.99),
                new SmartphoneEntity(2, "Apple iPhone 16", 99999.99),
                new SmartphoneEntity(3, "Google Pixel 9", 69999.99)
        );
        when(smartphoneRepository.findAll()).thenReturn(smartphones);

        List<SmartphoneEntity> result = smartphoneService.findAll();

        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
        assertEquals(3, result.get(2).getId());
    }

    //  Тесты для валидации

    @Test
    void save_shouldNotValidateNullModel() {
        // Текущая реализация не проверяет null - тест для фиксации поведения
        when(smartphoneRepository.save(any(SmartphoneEntity.class))).thenReturn(1);

        int id = smartphoneService.save(null, 1000.0);

        assertEquals(1, id);
    }

    @Test
    void save_shouldNotValidateNegativePrice() {
        // Текущая реализация не проверяет отрицательную цену - тест для фиксации поведения
        when(smartphoneRepository.save(any(SmartphoneEntity.class))).thenReturn(1);

        int id = smartphoneService.save("Test", -100.0);

        assertEquals(1, id);
    }
}
