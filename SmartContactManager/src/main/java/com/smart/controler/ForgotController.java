package com.smart.controler;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.helper.PasswordValidator;
import com.smart.model.User;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

	Random random = new Random(1000);

	@Autowired
	private EmailService emailservice;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private PasswordValidator passwordValidator;

	@RequestMapping("/forgot")
	public String openEmailForm() {

		return "forgot_email_form";
	}

	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email, Model model, HttpSession session) {

		System.out.println(email);
		// Otp otp Of 4digit

		int otp = random.nextInt(99999);
		System.out.println(otp);

		String subject = "OTP for Smart Contact Manager";
		String message = "<h1> OTP=" + otp + "</h1>";
		String to = email;

		boolean flag = this.emailservice.sendEmail(subject, message, to);

		if (flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);

			return "verify_otp";
		} else {

			session.setAttribute("message", "Check your email Id!! ");

			return "forgot_email_form";
		}

	}

	// verify otp

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {

		int myOtp = (int) session.getAttribute("myotp");
		String email = (String) session.getAttribute("email");

		if (myOtp == otp) {
			// passsword changed Form

			User user = this.userRepository.getUserByUserName(email);

			if (user == null) {

				session.setAttribute("message", "User Does not exits with this email!! ");

				return "forgot_email_form";
			} else {

			}

			return "password_changed_form";

		} else {
			session.setAttribute("message", "You have entered Wrong Otp");
			return "verify_otp";

		}

	}

	@PostMapping("/change-pasword")
	public String changePassword(@RequestParam("newPassword") String newPassword, HttpSession session) {

		String email = (String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(user);
		return "redirect:/signin?change=password change successfully..";
	}
}
