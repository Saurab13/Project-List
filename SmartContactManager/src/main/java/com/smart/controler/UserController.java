package com.smart.controler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.helper.Message;
import com.smart.model.Contact;
import com.smart.model.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// Method For adding Common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		model.addAttribute("user", user);
	}

	// Dashboard Home
	@RequestMapping("/index")
	public String Dashboard(Model model) {
		model.addAttribute("title", "User Dashbord");
		return "normal/user_dashboard";
	}

	// open add form handle
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult result,
			@RequestParam("profileImage") MultipartFile file, Principal principal, Model model, HttpSession session) {

		try {

			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			// processing & uploading file..

			if (file.isEmpty()) {
				System.out.println("File is Empty");
				contact.setImage("contact.png");

			} else {
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("image uploaded");
			}

			user.getContacts().add(contact);
			contact.setUser(user);

			this.userRepository.save(user);

			System.out.println("Data" + contact);

			System.out.println("Added to data base");

			/* Show Messages */
			session.setAttribute("message", new Message("Successfully Contact Addded!", "alert-success"));

		} catch (Exception e) {
			System.out.println("Error:" + e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something Went Wrong!! ", "alert-danger"));
			return "normal/add_contact_form";
		}

		return "normal/add_contact_form";
	}

	// Show Contact
	// page start from 0
	@GetMapping("/show-contact/{page}")
	public String showContact(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show-Contact-Page");

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);

		model.addAttribute("contacts", contacts);

		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	// Showing particular Contact Details

	@RequestMapping("/contact/{cid}")
	public String showContactDetails(@PathVariable("cid") Integer cid, Model model, Principal principal) {

		Optional<Contact> contactOptional = this.contactRepository.findById(cid);

		Contact contact = contactOptional.get();

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		return "normal/contact_detail";
	}

	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model model) {

		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();

		// delete photo

		contact.setUser(null);

		this.contactRepository.delete(contact);

		return "redirect:/user/show-contact/0";
	}

	// Open update Form handle
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model model) {

		model.addAttribute("title", "update Form");

		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		model.addAttribute("contact", contact);
		return "normal/update_form";
	}

	// process update Form
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute("contact") Contact contact,
			@RequestParam("profileImage") MultipartFile file, Principal principal) {

		try {
			// old Contact Details
			Contact oldContactDetail = this.contactRepository.findById(contact.getCid()).get();

			if (!file.isEmpty()) {

				// delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContactDetail.getImage());
				file1.delete();

				// update new Photo

				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(oldContactDetail.getImage());

			}

			User user = this.userRepository.getUserByUserName(principal.getName());

			contact.setUser(user);
			this.contactRepository.save(contact);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/user/contact/" + contact.getCid();
	}

	@GetMapping("/profile")
	public String yourProfile(Model model, Principal principal) {
		model.addAttribute("title", "Profile Page");
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		model.addAttribute("user", user);

		return "normal/profile";
	}

	// open Setting handler
	@GetMapping("/settings")
	public String openSetting() {
		return "normal/settings";

	}

	// Change password handle
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, Model model, HttpSession session) {
		String username = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(username);

		if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {

			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("your Password is Succesfully Changed !", "alert-success"));

		} else {
			// error
			session.setAttribute("message", new Message("Please Enter Correct Old Password !", "alert-success"));

			return "redirect:/user/settings";

		}
		return "redirect:/user/index";
	}

}
