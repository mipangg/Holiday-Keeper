package io.mipangg.holidaykeeper.domain.type.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.type.entity.Type;
import io.mipangg.holidaykeeper.domain.type.repository.TypeRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TypeServiceTests {

    @InjectMocks
    private TypeService typeService;

    @Mock
    private TypeRepository typeRepository;

    @Test
    @DisplayName("이미 저장되어 있는 type인 경우 기존의 것을, 아닌 경우 type을 새로 저장 후 한꺼번에 반환")
    void getOrCreateTypesTest() {

        Set<String> typeNames = Set.of("Public", "Bank");
        Type typePublic = Type.builder().type("Public").build();

        when(typeRepository.findByTypeIn(typeNames)).thenReturn(List.of(typePublic));

        Map<String, Type> result = typeService.getOrCreateTypes(typeNames);

        verify(typeRepository).findByTypeIn(typeNames);
        verify(typeRepository).saveAll(anyList());

        assertThat(result.get("Public").getType()).isEqualTo("Public");
        assertThat(result.get("Bank").getType()).isEqualTo("Bank");

    }

}