package io.mipangg.holidaykeeper.domain.type.service;

import io.mipangg.holidaykeeper.domain.type.entity.Type;
import io.mipangg.holidaykeeper.domain.type.repository.TypeRepository;
import java.util.ArrayList;
import java.util.HashMap;
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
        Map<String, Type> result = new HashMap<>();
        List<Type> existingTypes = typeRepository.findByTypeIn(typeNames);
        if (existingTypes != null && !existingTypes.isEmpty()) {
            existingTypes.forEach(type -> result.put(type.getType(), type));
        }

        List<Type> newTypes = new ArrayList<>();

        for (String typeName : typeNames) {
            Type type = result.get(typeName);
            if (type == null) {
                type = Type.builder()
                        .type(typeName)
                        .build();
                newTypes.add(type);
                result.put(typeName, type);
            }
        }

        if (!newTypes.isEmpty()) {
            typeRepository.saveAll(newTypes);
        }

        return result;
    }
}
