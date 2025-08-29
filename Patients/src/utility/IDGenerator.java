package utility;

import java.util.HashSet;
import java.util.Set;

public class IDGenerator {

    private String prefix;
    private int counter;
    private final int start;
    private final int max;
    private Set<String> usedIDs;

    public IDGenerator(String prefix, int start, int max) {
        this.prefix = prefix;
        this.start = start;
        this.max = max;
        this.counter = start;
        this.usedIDs = new HashSet<>();
    }

    public String generateID() {
        int attempts = 0; // prevent infinite loop
        while (attempts <= (max - start + 1)) {
            String id = prefix + counter;
            counter++;
            if (counter > max) counter = start; // wrap around

            if (!usedIDs.contains(id)) {
                usedIDs.add(id);
                return id;
            }
            attempts++;
        }
        throw new RuntimeException("No available IDs left!");
    }

    public void releaseID(String id) {
        usedIDs.remove(id);
    }

}
