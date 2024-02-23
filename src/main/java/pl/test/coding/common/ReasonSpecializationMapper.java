package pl.test.coding.common;

import org.springframework.stereotype.Component;
import pl.test.coding.doctor.exception.SpecializationNotFoundException;

import static pl.test.coding.common.Specialization.*;

@Component
public class ReasonSpecializationMapper {
    public Specialization mapReasonToSpecialization(ReasonForVisit reason) {
        return switch (reason) {
            case ANXIETY -> PSYCHIATRY;
            case ASTHMA -> PULMONOLOGY;
            case BACK_PROBLEM -> CARDIOLOGY;
            case SKIN_DISORDER, CHOLESTEROL_PROBLEM -> DERMATOLOGY;
            case MIGRAINE, DIABETE, JOINT_PAINT -> FAMILY_MEDICINE;
            case HIGH_BLOOD_PRESSURE -> HERMATOLOGY;
            default -> throw new SpecializationNotFoundException();
        };
    }
}
