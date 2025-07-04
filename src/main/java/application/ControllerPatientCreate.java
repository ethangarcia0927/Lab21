package application;

import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.Patient;
import application.model.PatientRepository;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import view.PatientView;

import java.sql.*;

@Controller
public class ControllerPatientCreate {
    /*
     * Controller class for patient interactions.
     *   register as a new patient.
     *   update patient profile.
     */

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    SequenceService sequence;


    /*
     * Request blank patient registration form.
     */
    @GetMapping("/patient/new")
    public String getNewPatientForm(Model model) {
        // return blank form for new patient registration
        model.addAttribute("patient", new PatientView());
        return "patient_register";
    }

    /*
     * Process data from the patient_register form
     */
    @PostMapping("/patient/new")
    public String createPatient(PatientView p, Model model) {

        /*
         * validate doctor last name and find the doctor id
         */

        Doctor primaryDoc = doctorRepository.findByLastName(p.getPrimaryName());

        if (primaryDoc == null) {
            model.addAttribute("message", "SQL Error, Primary Doctor WAS NOT found.");
            model.addAttribute("patient", p);
            return "patient_register";
        }

        //Need to handle id no longer being auto-generated.
        int newPatientID = sequence.getNextSequence("PATIENT_SEQUENCE");
        // create a model.patient instance
        // copy data from PatientView to model
        Patient patientM = new Patient();
        patientM.setId(newPatientID);
        patientM.setFirstName(p.getFirstName());
        patientM.setLastName(p.getLastName());
        patientM.setBirthdate(p.getBirthdate());
        patientM.setSsn(p.getSsn());
        patientM.setStreet(p.getStreet());
        patientM.setCity(p.getCity());
        patientM.setState(p.getState());
        patientM.setZipcode(p.getZipcode());
        patientM.setPrimaryName(p.getPrimaryName());

        patientM.setId(newPatientID);
        patientRepository.insert(patientM);

        model.addAttribute("message", "Registration successful.");
        p.setId(newPatientID);
        model.addAttribute("patient", p);
        return "patient_show";
    }

    /*
     * Request blank form to search for patient by id and name
     */
    @GetMapping("/patient/edit")
    public String getSearchForm(Model model) {
        model.addAttribute("patient", new PatientView());
        return "patient_get";
    }

    /*
     * Perform search for patient by patient id and name.
     */
    @PostMapping("/patient/show")
    public String showPatient(PatientView p, Model model) {

        // TODO   search for patient by id and name

        // if found, return "patient_show", else return error message and "patient_get"
        Patient patient = patientRepository.findByIdAndLastName(p.getId(), p.getLastName());
        if (patient != null) {
            //copy data from model to view
            p.setFirstName(patient.getFirstName());
            p.setLastName(patient.getLastName());
            p.setBirthdate(patient.getBirthdate());
            p.setSsn(patient.getSsn());
            p.setStreet(patient.getStreet());
            p.setCity(patient.getCity());
            p.setState(patient.getState());
            p.setZipcode(patient.getZipcode());
            p.setPrimaryName(patient.getPrimaryName());
            model.addAttribute("patient", p);
            return "patient_show";

        } else {
            model.addAttribute("message", "SQL Error, Patient not found.");
            model.addAttribute("patient", p);
            return "patient_get";
        }
    }
}


