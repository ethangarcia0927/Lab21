package application;

import application.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import view.PrescriptionView;

import java.util.Optional;

  /*TODO: Copy this class from Lab 19 and modify
     Use ControllerDoctor.java for reference
     Add Entity classes and Repository
     Add Instance variables for SequenceService and repository interfaces

     You may make changes to any of the model classes and repository interfaces as needed and add any customized query methods
   */

@Controller
public class ControllerPrescriptionFill {

  @Autowired
  private PrescriptionRepository prescriptionRepository;

  @Autowired
  private DrugRepository drugRepository;

  @Autowired
  private PharmacyRepository pharmacyRepository;

  @Autowired
  private DoctorRepository doctorRepository;

  @GetMapping("/prescription/fill")
  public String getFillForm(Model model) {
    model.addAttribute("prescription", new PrescriptionView());
    return "prescription_fill";
  }

  @PostMapping("/prescription/fill")
  public String processFillForm(PrescriptionView view, Model model) {
    Optional<Prescription> presc = prescriptionRepository.findById(view.getRxid());
    if (presc.isEmpty()) {
      model.addAttribute("message", "Prescription not found.");
      model.addAttribute("prescription", view);
      return "prescription_fill";
    }

    if (presc.get().getRefills() <= 0) {
      model.addAttribute("message", "No more refills available.");
      model.addAttribute("prescription", view);
      return "prescription_fill";
    }

    Pharmacy pharmacy = pharmacyRepository.findByNameAndAddress(view.getPharmacyName(), view.getPharmacyAddress());
    if (pharmacy == null) {
      model.addAttribute("message", "Pharmacy not found.");
      model.addAttribute("prescription", view);
      return "prescription_fill";
    }

    Optional<Drug> drugOpt = drugRepository.findById(presc.get().getRxid());
    if (drugOpt.isEmpty()) {
      model.addAttribute("message", "Drug not found.");
      model.addAttribute("prescription", view);
      return "prescription_fill";
    }
    Drug drug = drugOpt.get();

    double cost = 0.0;
    for (Pharmacy.DrugCost dc : pharmacy.getDrugCosts()) {
      if (dc.getDrugName().equalsIgnoreCase(drug.getName())) {
        cost = dc.getCost();
        break;
      }
    }

    Prescription.FillRequest request = new Prescription.FillRequest();
    request.setPharmacyID(pharmacy.getId());
    request.setDateFilled(java.time.LocalDate.now().toString());
    request.setCost(String.format("%.2f", cost));
    presc.get().getFills().add(request);
    presc.get().setRefills(presc.get().getRefills() - 1);
    prescriptionRepository.save(presc.get());

    view.setDoctorId(presc.get().getDoctorId());
    Optional<Doctor> doctorOpt = Optional.ofNullable(
        doctorRepository.findById(presc.get().getDoctorId()));
    if (doctorOpt.isPresent()) {
      view.setDoctorFirstName(doctorOpt.get().getFirstName());
      view.setDoctorLastName(doctorOpt.get().getLastName());
    }
    view.setRefillsRemaining(presc.get().getRefills());
    view.setPharmacyID(pharmacy.getId());
    view.setDrugName(drug.getName());
    view.setPharmacyPhone(pharmacy.getPhone());
    view.setDateFilled(request.getDateFilled());
    view.setCost(String.format("%.2f", cost));

    model.addAttribute("message", "Prescription filled.");
    model.addAttribute("prescription", view);
    return "prescription_show";
  }
}
