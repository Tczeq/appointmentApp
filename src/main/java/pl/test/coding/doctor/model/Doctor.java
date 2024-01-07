package pl.test.coding.doctor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import pl.test.coding.common.Specialization;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Doctor")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Pattern(regexp = "[A-Z][a-z]{1,50}", message = "The name must begin with a capital letter and contain from 1 to 50 letters.")
    private String name;
    @Pattern(regexp = "[A-Z][a-z]{1,50}", message = "The lastname must begin with a capital letter and contain from 1 to 50 letters.")
    private String lastName;

    @JoinColumn(name = "reason_for_visit")
    @Enumerated(EnumType.STRING)
    private Specialization specialization;
    @Version
    private Integer version;
    @Override
    public String toString() {
        return name + " " + lastName + " " + specialization;
    }

}
