package pl.test.coding.doctor.model.dto;

import lombok.Builder;
import lombok.Getter;
import pl.test.coding.common.Specialization;
import pl.test.coding.doctor.model.Doctor;

@Builder
@Getter
public class DoctorDto {

    private int id;
    private String name, lastName;
    private Specialization specialization;

    public static DoctorDto fromEntity(Doctor doctor) {
        return DoctorDto.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .lastName(doctor.getLastName())
                .specialization(doctor.getSpecialization())
                .build();
    }

    @Override
    public String toString() {
        return name + " " + lastName + " " + specialization;
    }
}
