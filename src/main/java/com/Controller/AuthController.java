package com.Controller;

import com.Model.User;
import com.Repository.UserRepository;
import com.Service.UserService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.security.Principal;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/landingPage")
    public String landingPage(Model model, HttpSession session) {
        String signupSuccess = (String) session.getAttribute("signupSuccess");
        if (signupSuccess != null) {
            model.addAttribute("signupSuccess", signupSuccess);
            session.removeAttribute("signupSuccess"); // remove it after showing once
        }
        return "/landingPage";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {

        model.addAttribute("user", new User());
        return "signup";
    }

//    @PostMapping("/signup")
//    public String registerUser(@ModelAttribute("user") User user, HttpSession session, Model model) {
//
//
//        userService.saveUser(user);
//        session.setAttribute("signupSuccess", "Signup successful! Please login.");
//
//        return "redirect:/landingPage";
//    }


    @PostMapping("/signup")
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            if (result.hasErrors()) {
                model.addAttribute("user", user);
                return "signup";
            }

            if (userRepository.existsByEmail(user.getEmail())) {
                model.addAttribute("error", "Email already exists");
                return "signup";
            }

            userService.saveUser(user);
            session.setAttribute("signupSuccess", "Signup successful! Please login.");
            return "redirect:/landingPage";

        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred during signup.");
            return "signup";
        }
    }



    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
//
//    @PostMapping("/login")
//    public String loginUser(@RequestParam String email,
//                            @RequestParam String password,
//                            HttpSession session,
//                            Model model) {
//
//
//
//        Optional<User> optionalUser = userService.findByEmail(email);
//        if (optionalUser.isPresent() && optionalUser.get().getPassword().equals(password)) {
//            User user = optionalUser.get();
//            session.setAttribute("loggedInUser", user);
//            session.setAttribute("role", user.getRole());
//
//
//            if (user.getRole().equals("Admin")) {
//                User user = userRepository.findByEmail(email).orElse(null);
//
//          if (user != null && user.getPassword().equals(password)) {
//          session.setAttribute("loggedInUser", user); //  store user in session
//            return "redirect:/profile";
//       } else {
//            model.addAttribute("error", "Invalid email or password");
//           return "login";
//       }
//
//                session.setAttribute("loginSuccess", "login successful! Welcome in Admin Dashboard");
//                return "redirect:/dashboard";
//
//            } else if (user.getRole().equals("Student")) {
//                session.setAttribute("loginSuccess", "login successful! Welcome in Student Dashboard");
//                return "redirect:/studentdashboard";
//
//            } else {
//                return "redirect:/signup";
//            }
//        } else {
//            model.addAttribute("error", "Invalid credentials");
//            return "login";
//        }
//    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        Optional<User> optionalUser = userService.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getPassword().equals(password)) {
                session.setAttribute("loggedInUser", user);
                session.setAttribute("role", user.getRole());

                if (user.getRole().equalsIgnoreCase("Admin")) {
                    session.setAttribute("loginSuccess", "Login successful! Welcome to the Admin Dashboard.");
                    return "redirect:/dashboard";

                } else if (user.getRole().equalsIgnoreCase("Student")) {
                    session.setAttribute("loginSuccess", "Login successful! Welcome to the Student Dashboard.");
                    return "redirect:/studentdashboard";

                } else {
                    // Unknown role – redirect to signup or show error
                    model.addAttribute("error", "Unknown role. Please register again.");
                    return "redirect:/signup";
                }

            } else {
                model.addAttribute("error", "Invalid email or password");
                return "login";
            }

        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }


//    @GetMapping("/profile")
//    public String showProfile(Model model, Principal principal) {
//        // Get logged-in user's email/username
//        String email = principal.getName();
//
//        // Fetch student by email or username
//        User user = UserRepository.findByEmail(email);
//
//        // Add to model
//        model.addAttribute("User", user);
//        return "profile";  // this is profile.html
//    }


//    @PostMapping("/login")
//    public String loginUser(@RequestParam String email,
//                            @RequestParam String password,
//                            HttpSession session,
//                            Model model) {
//        User user = userRepository.findByEmail(email).orElse(null);
//
//        if (user != null && user.getPassword().equals(password)) {
//            session.setAttribute("loggedInUser", user); // ✅ store user in session
//            return "redirect:/profile";
//        } else {
//            model.addAttribute("error", "Invalid email or password");
//            return "login";
//        }
//    }


    @GetMapping("/profile")
    public String showProfilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login"; // Not logged in
        }

        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            // You can either send a reset link OR show the change password form directly
            // Here we redirect to a reset-password form
            model.addAttribute("email", email);
            return "redirect:/reset-password?email=" + email;
        } else {
            model.addAttribute("error", "Email not found.");
            return "forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(HttpSession session, Model model,
                                        @RequestParam(value = "email", required = false) String emailParam) {
        // Use session-stored user if available
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String email;

        if (loggedInUser != null) {
            email = loggedInUser.getEmail(); // Get email from session
        } else if (emailParam != null && !emailParam.isEmpty()) {
            email = emailParam; // Fallback to request param
        } else {
            model.addAttribute("error", "Email not available for password reset.");
            return "login"; // Redirect or show error
        }

        model.addAttribute("email", email);
        return "reset-password";
    }


    @PostMapping("/reset-password")
    public String resetPassword(HttpSession session,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "reset-password";
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            loggedInUser.setPassword(newPassword);
            userService.saveUser(loggedInUser);
            session.setAttribute("loginSuccess", "Password changed successfully.");
            return "redirect:/profile";
        } else {
            model.addAttribute("error", "User session expired. Please login again.");
            return "login";
        }
    }

    @GetMapping("/Close")
    public String backToDashboard(HttpSession session) {
        // Step 1: Get the logged-in user from session
        User user = (User) session.getAttribute("loggedInUser");

        // Step 2: If user is not found, redirect to login
        if (user == null) {
            return "redirect:/login";
        }

        // Step 3: Get email and role
        String email = user.getEmail();  //  this is how you get the email
        String role = user.getRole();    //  get the role for dashboard redirect

        // Step 4: Redirect based on role
        if (role.equalsIgnoreCase("Admin")) {
            return "redirect:/dashboard"; // Admin dashboard
        } else if (role.equalsIgnoreCase("Student")) {
            return "redirect:/studentdashboard"; // Student dashboard
        } else {
            return "redirect:/login"; // Unknown role
        }
    }





    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
