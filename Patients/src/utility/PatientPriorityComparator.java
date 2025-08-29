package utility;

import entity.Patient;
import java.util.Comparator;

public class PatientPriorityComparator implements Comparator<Patient> {

    @Override
public int compare(Patient p1, Patient p2) {
    // 1. Compare by custom priority rank
    int priorityComparison = Integer.compare(getPriorityRank(p1.getPriority()),
                                             getPriorityRank(p2.getPriority()));
    if (priorityComparison != 0) {
        return priorityComparison;
    }

    // 2. If same priority → compare by arrival time
    int arrivalComparison = p1.getRegistrationTime().compareTo(p2.getRegistrationTime());
    if (arrivalComparison != 0) {
        return arrivalComparison;
    }

    // 3. If same arrival → older first
    int ageComparison = Integer.compare(p2.getAge(), p1.getAge());
    if (ageComparison != 0) {
        return ageComparison;
    }

    // 4. If still tied → compare by status if both present
    if (p1.getStatus() != null && p2.getStatus() != null) {
        return p1.getStatus().compareTo(p2.getStatus());
    }

    return 0;
}

private int getPriorityRank(String priority) {
    if (priority == null) return 99;
    switch (priority.toLowerCase()) {
        case "emergency": return 1;
        case "elderly":   return 2;
        case "normal":    return 3;
        default:          return 99;
    }
}

    }
