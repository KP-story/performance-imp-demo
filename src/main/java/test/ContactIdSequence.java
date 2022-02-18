package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContactIdSequence implements SequenceIdRepo {
    private static final String CONTACT_SEQUENCE_NAME = "test";

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Long> getNextContactIds(int numbers) {
        return _next(numbers, CONTACT_SEQUENCE_NAME);
    }

    @Override
    public Long getNextContactId() {
        return _next(CONTACT_SEQUENCE_NAME);
    }

    public List<Long> _next(int numbers, String sequence) {
        Query q = entityManager.createNativeQuery(String.format("select nextval('%s') from generate_series(1, %s)", sequence, numbers));
        List<BigInteger> rs = q.getResultList();
        return rs.stream().map(BigInteger::longValue).collect(Collectors.toList());
    }

    public Long _next(String sequence) {
        Query q = entityManager.createNativeQuery(String.format("select nextval('%s')", sequence));
        return ((BigInteger) q.getSingleResult()).longValue();
    }
}
