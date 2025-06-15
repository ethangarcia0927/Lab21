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
  /*TODO: Copy this class from Lab 19 and modify
     Use ControllerDoctor.java for reference
     Add Entity classes and Repository
     Add Instance variables for SequenceService and repository interfaces

     You may make changes to any of the model classes and repository interfaces as needed and add any customized query methods
   */


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

        patientRepository.insert(patientM);

        Doctor primaryDoc = doctorRepository.findByLastName(p.getPrimaryName());

        if (primaryDoc == null) {
            model.addAttribute("message", "SQL Error, Primary Doctor WAS NOT found.");
            model.addAttribute("patient", p);
            return "patient_register";
        }

        model.addAttribute("message", "Registration successful.");
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
        Patient patient = patientRepository.findByIdAndFirstNameAndLastName(p.getId(), p.getFirstName(), p.getLastName());
        if (patient != null) {
            patient.setFirstName(p.getFirstName());
            patient.setLastName(p.getLastName());
            patient.setBirthdate(p.getBirthdate());
            patient.setSsn(p.getSsn());
            patient.setStreet(p.getStreet());
            patient.setCity(p.getCity());
            patient.setState(p.getState());
            patient.setZipcode(p.getZipcode());
            patient.setPrimaryName(p.getPrimaryName());
            model.addAttribute("patient", patient);
            return "patient_show";
        } else {
            model.addAttribute("message", "SQL Error, Patient not found.");
            model.addAttribute("patient", p);
            return "patient_get";
        }
    }

    /*
     * return JDBC Connection using jdbcTemplate in Spring Server
     */

//    private Connection getConnection() throws SQLException {
//        Connection conn = jdbcTemplate.getDataSource().getConnection();
//        return conn;
//    }
}


