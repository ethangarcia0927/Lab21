package application;

import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.Patient;
import application.model.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import view.PatientView;

@Controller
public class ControllerPatientUpdate {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;
    /*
     *  Display patient profile for patient id.
     */
    @GetMapping("/patient/edit/{id}")
    public String getUpdateForm(@PathVariable int id, Model model) {

        Patient patient = patientRepository.findByIdAndLastName(id, patientRepository.findById(id).get().getLastName());
        if (patient == null) {
            model.addAttribute("message", "Patient IS NOT found");
            return "index";
        }

        PatientView patientView = new PatientView();
        patientView.setId(id);
        patientView.setFirstName(patient.getFirstName());
        patientView.setLastName(patient.getLastName());
        patientView.setPrimaryName(patient.getPrimaryName());
        patientView.setStreet(patient.getStreet());
        patientView.setCity(patient.getCity());
        patientView.setState(patient.getState());
        patientView.setZipcode(patient.getZipcode());
        patientView.setBirthdate(patient.getBirthdate());
        patientView.setSsn(patient.getSsn());
        model.addAttribute("message", "Edit Patient");
        model.addAttribute("patient", patientView);
        return "patient_edit";

        // TODO search for patient by id
        //  if not found, return to home page using return "index";
        //  else create PatientView and add to model.

        // model.addAttribute("message", some message);
        // model.addAttribute("patient", pv
        // return editable form with patient data

    }


    /*
     * Process changes from patient_edit form
     *  Primary doctor, street, city, state, zip can be changed
     *  ssn, patient id, name, birthdate, ssn are read only in template.
     */
    @PostMapping("/patient/edit")
    public String updatePatient(PatientView p, Model model) {

        // validate doctor last name
        // TODO
        try {
            Doctor primaryDoc = doctorRepository.findByLastName(p.getPrimaryName());
            if (primaryDoc == null) {
                model.addAttribute("message", "Doctor not found");
                model.addAttribute("patient", p);
                return "patient_edit";
            }

            Patient patient = patientRepository.findByIdAndLastName(p.getId(), p.getLastName());
            if (patient == null) {
            model.addAttribute("message", "Patient IS NOT found");
            model.addAttribute("patient", p);
            return "patient_edit";
            }

            patient.setStreet(p.getStreet());
            patient.setCity(p.getCity());
            patient.setState(p.getState());
            patient.setZipcode(p.getZipcode());
            patient.setPrimaryName(p.getPrimaryName());

            // TODO update patient profile data in database

            patientRepository.save(patient);

            model.addAttribute("message", "Patient updated");
            model.addAttribute("patient", p);
            return "patient_show";
        } catch (Exception e) {
            model.addAttribute("message", "Update failed: " + e.getMessage());
            model.addAttribute("patient", p);
            return "patient_edit";
        }


//        try {
//            String sqlDoctorLastName = "select id from doctor where last_name = ?";
//            Integer doc_id = jdbcTemplate.queryForObject(sqlDoctorLastName, new Object[]{p.getPrimaryName()}, Integer.class);
//
//            if (doc_id == null) {
//                model.addAttribute("message", "Doctor not found.");
//                model.addAttribute("patient", p);
//                return "patient_edit";
//            }
//
//            String sqlUpdate = "update patient set addressStreet = ?, city = ?, state = ?, zip_code = ?, doctor_id = ? where patient_id = ?";
//            jdbcTemplate.update(sqlUpdate, p.getStreet(), p.getCity(), p.getState(), p.getZipcode(), doc_id, p.getId());
//
//            model.addAttribute("message", "Patient updated.");
//            model.addAttribute("patient", p);
//            return "patient_show";
//
//        } catch (Exception e) {
//            model.addAttribute("message", "Update error, check Doctor name is correct. \nProgram error: \n" + e.getMessage());
//            model.addAttribute("patient", p);
//            return "patient_edit";
//        }

    }


}


