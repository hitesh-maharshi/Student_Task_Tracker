//package com.Controller;
//
//import com.Model.User;
//import com.Service.TaskService;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@Controller
//public class StudentController {
//
//    @Autowired
//    public TaskService taskService;
//
//    @GetMapping("/student")
//    public String showStudentDetail(Model model, HttpSession session) {
//
//            User user = (User) session.getAttribute("loggedInUser");
//            if (user == null) {
//                return "redirect:/login";
//            }
//
//
//            model.addAttribute("taskList", taskService.getTasksByUser(user));
//        return "student";
//        }
//
//
//
//
//}
