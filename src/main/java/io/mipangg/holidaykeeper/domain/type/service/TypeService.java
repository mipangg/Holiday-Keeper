package io.mipangg.holidaykeeper.domain.type.service;

import io.mipangg.holidaykeeper.domain.type.entity.Type;
import io.mipangg.holidaykeeper.domain.type.repository.TypeRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TypeService {

    private final TypeRepository typeRepository;

    @Transactional
    public Map<String, Type> getOrCreateTypes(Set<String> typeNames) {

        Map<String, Type> types = new HashMap<>();

        // repository에 이미 존재하는 type 조회
        typeRepository.findByTypeIn(typeNames).forEach(type ->
                types.put(type.getType(), type)
        );

        // existingTypes에 존재하는 type이면 반환용 리스트에만 저장
        // 새로운 type이면 newTypes에도 저장
        List<Type> newTypes = new ArrayList<>();
        for (String typeName : typeNames) {
            Type type = types.get(typeName);
            if (type == null) {
                type = Type.builder()
                        .type(typeName)
                        .build();
                newTypes.add(type);
                types.put(typeName, type);
            }
        }

        // 새로운 type 리스트 한번에 저장
        if (!newTypes.isEmpty()) {
            typeRepository.saveAll(newTypes);
        }

        return types;
    }

}
