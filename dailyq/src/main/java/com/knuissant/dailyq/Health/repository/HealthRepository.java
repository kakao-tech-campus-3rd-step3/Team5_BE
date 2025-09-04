package com.knuissant.dailyq.Health.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.TupleTransformer;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HealthRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Map<String, Object>> findHealthDump(int limit) {
        String sql = "select * from health limit :limit";
        @SuppressWarnings("unchecked")
        NativeQuery<Object[]> query = (NativeQuery<Object[]>) entityManager
                .createNativeQuery(sql)
                .unwrap(NativeQuery.class);

        query.setParameter("limit", limit);

        query.setTupleTransformer(new TupleTransformer<Map<String, Object>>() {
            @Override
            public Map<String, Object> transformTuple(Object[] tuple, String[] aliases) {
                Map<String, Object> row = new HashMap<>();
                if (aliases != null) {
                    for (int i = 0; i < aliases.length; i++) {
                        String key = aliases[i] != null ? aliases[i] : ("col" + i);
                        row.put(key, tuple[i]);
                    }
                } else {
                    for (int i = 0; i < tuple.length; i++) {
                        row.put("col" + i, tuple[i]);
                    }
                }
                return row;
            }
        });

        List<Map<String, Object>> results = new ArrayList<>();
        for (Object result : query.getResultList()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> row = (Map<String, Object>) result;
            results.add(row);
        }
        return results;
    }
}


