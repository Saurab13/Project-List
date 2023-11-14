package com.smart.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.helper.Message;
import com.smart.model.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home-Smart contact Manager");
		return "home";
	}

	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "Home-Smart contact Manager");
		return "about";
	}

	@RequestMapping("/signup")
	public String signUp(Model model) {
		model.addAttribute("title", "Register-Smart Contact Manager");
		model.addAttribute("user", new User());

		return "signup";
	}

	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		try {
			if (!agreement) {
				throw new Exception("You have not agreed to the terms and conditions");
			}

			if (result.hasErrors()) {
				// System.out.println("Error:"+result.toString());
				model.addAttribute("user", user);
				return "signup";

			}

			// Set user properties
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			// Save the user to the repository
			userRepository.save(user);

			// Reset the user object and set a success message
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));

			return "signup";
		} catch (Exception e) {
			// User input validation error
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something Went Wrong!! ", "alert-danger"));
			return "signup";
		}
	}

	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login Page");
		return "login";
	}

}
