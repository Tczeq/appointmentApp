package pl.test.coding.appointment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import pl.test.coding.common.ReasonForVisit;
import pl.test.coding.doctor.model.Doctor;
import pl.test.coding.patient.model.Patient;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@SQLDelete(sql = "UPDATE appointment SET deleted = 1 WHERE id = ?")
@Table(name = "Appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime term;

    @Enumerated(EnumType.STRING)
    private ReasonForVisit reasonForVisit;
    private Integer appointmentTime;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "deleted")
    private Boolean isDeleted = false;

    @Override
    public String toString() {
        return "Appointment{" +
                "term=" + term +
                ", reasonForVisit=" + reasonForVisit +
                ", appointmentTime=" + appointmentTime +
                ", doctor=" + doctor +
                ", patient=" + patient +
                '}';
    }
}
