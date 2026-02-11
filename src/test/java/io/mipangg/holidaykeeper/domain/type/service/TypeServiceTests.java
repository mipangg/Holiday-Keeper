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
    @DisplayName("repository에 type이 존재하면 조회, 없으면 생성 후 저장하는 기능 테스트")
    void getOrCreateTypesTest() {

        Set<String> typeNames = Set.of("Public", "Bank");
        Type typeBank = Type.builder().type("Bank").build();

        when(typeRepository.findByTypeIn(typeNames)).thenReturn(List.of(typeBank));

        Map<String, Type> actual = typeService.getOrCreateTypes(typeNames);

        verify(typeRepository).findByTypeIn(typeNames);
        verify(typeRepository).saveAll(anyList());

        assertThat(actual.get("Public").getType()).isEqualTo("Public");
        assertThat(actual.get("Bank").getType()).isEqualTo("Bank");

    }

}