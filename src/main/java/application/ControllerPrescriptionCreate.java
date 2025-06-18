package application;

import application.model.*;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import view.PrescriptionView;

import java.util.Optional;

@Controller
public class ControllerPrescriptionCreate {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private SequenceService sequence;

    @GetMapping("/prescription/new")
    public String getPrescriptionForm(Model model) {
        model.addAttribute("prescription", new PrescriptionView());
        return "prescription_create";
    }

    @PostMapping("/prescription")
    public String createPrescription(PrescriptionView view, Model model) {
        Doctor d = doctorRepository.findById(view.getDoctorId());
        if (d == null || !d.getLastName().equalsIgnoreCase(view.getDoctorLastName()) || !d.getFirstName().equalsIgnoreCase(view.getDoctorFirstName())) {
            model.addAttribute("message", "Doctor ID/name mismatch.");
            model.addAttribute("prescription", view);
            return "prescription_create";
        }

        Optional<Patient> patientOpt = patientRepository.findById(view.getPatientId());
        if (patientOpt.isEmpty() || !patientOpt.get().getLastName().equalsIgnoreCase(view.getPatientLastName()) || !patientOpt.get().getFirstName().equalsIgnoreCase(view.getPatientFirstName())) {
            model.addAttribute("message", "Patient ID/name mismatch.");
            model.addAttribute("prescription", view);
            return "prescription_create";
        }
        Patient p = patientOpt.get();
        if (p == null || !p.getLastName().equalsIgnoreCase(view.getPatientLastName()) || !p.getFirstName().equalsIgnoreCase(view.getPatientFirstName())) {
            model.addAttribute("message", "Patient ID/name mismatch.");
            model.addAttribute("prescription", view);
            return "prescription_create";
        }

        Drug drug = drugRepository.findByName(view.getDrugName());
        if (drug == null) {
            model.addAttribute("message", "Drug not found.");
            model.addAttribute("prescription", view);
            return "prescription_create";
        }

        int rxid = sequence.getNextSequence("PRESCRIPTION_SEQUENCE");
        Prescription presc = new Prescription();
        presc.setRxid(rxid);
        presc.setDoctorId(view.getDoctorId());
        presc.setPatientId(view.getPatientId());
        presc.setDrugName(view.getDrugName());
        presc.setQuantity(view.getQuantity());
        presc.setRefills(view.getRefills());

        prescriptionRepository.insert(presc);

        view.setRxid(rxid);
        model.addAttribute("message", "Prescription created.");
        model.addAttribute("prescription", view);
        return "prescription_show";
    }

}
