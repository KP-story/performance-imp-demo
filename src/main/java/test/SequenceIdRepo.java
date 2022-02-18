package test;

import java.util.List;

public interface SequenceIdRepo {
    List<Long> getNextContactIds(int numbers);

    Long getNextContactId();

}
