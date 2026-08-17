package com.Controller;

import com.Model.Task;
import com.Model.User;
import com.Repository.TaskRepository;
import com.Repository.UserRepository;
import com.Service.TaskService;
import com.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class TaskController {


    @Autowired
    public TaskService taskService;

    @Autowired
    public UserService userService;

    @Autowired
    public TaskRepository taskRepository;



//    private final UserService userService;
//
//    @Autowired
//    public TaskController(TaskService taskService, UserService userService) {
//        this.taskService = taskService;
//        this.userService = userService;
//    }

//    @GetMapping("/dashboard")
//    public String viewDashboard(Model model, HttpSession session) {
//        User user = (User) session.getAttribute("loggedInUser");
//
//        String loginSuccess = (String) session.getAttribute("loginSuccess");
//        if (loginSuccess != null) {
//            model.addAttribute("loginSuccess", loginSuccess);
//            session.removeAttribute("loginSuccess"); // remove it after showing once
//        }
//        if (user == null) {
//            return "redirect:/login";
//        }
//        List<Task> taskList = taskRepository.findByUser(user);
//        model.addAttribute("taskList", taskList);
//        return "dashboard";
//    }
//
//    @GetMapping("/add-task")
//    public String showAddTaskForm(Model model, HttpSession session) {
//        User user = (User) session.getAttribute("loggedInUser");
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        model.addAttribute("task", new Task());
//        model.addAttribute("names", userService.getName());
//        return "dashboard";
//    }
//
//    @PostMapping("/add-task")
//    public String addTask(@ModelAttribute("task") Task task,
//                          HttpSession session,
//                          Model model) {
//        User user = (User) session.getAttribute("loggedInUser");
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        try {
//            taskService.addTask(task, user);
//            return "redirect:/dashboard";
//        } catch (Exception e) {
//            model.addAttribute("error", "Failed to add task: " + e.getMessage());
//            model.addAttribute("names", userService.getName());
//            return "add_task";
//        }
//    }

    @GetMapping("/dashboard")
    public String viewDashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        // Flash login success message
        String loginSuccess = (String) session.getAttribute("loginSuccess");
        if (loginSuccess != null) {
            model.addAttribute("loginSuccess", loginSuccess);
            session.removeAttribute("loginSuccess");
        }

        // Task list for current user
        List<Task> taskList = taskRepository.findByUser(user);
        model.addAttribute("taskList", taskList);

        //  Important for modal form
        model.addAttribute("task", new Task()); // for form binding
        model.addAttribute("names", userService.getName()); // for student dropdown

        return "dashboard";
    }

    @PostMapping("/add-task")
    public String addTask(@ModelAttribute("task") Task task,
                          HttpSession session,
                          Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            // Save the task
            taskService.addTask(task, user);
            return "redirect:/dashboard";
        } catch (Exception e) {
            // Error occurred, reload dashboard with modal open
            List<Task> taskList = taskRepository.findByUser(user);
            model.addAttribute("taskList", taskList);
            model.addAttribute("task", task); // keep user-filled form data
            model.addAttribute("names", userService.getName());
            model.addAttribute("error", "Failed to add task: " + e.getMessage());
            model.addAttribute("openModal", true); // signal to reopen modal

            return "dashboard";
        }
    }


//    @GetMapping("/tasks/edit/{id}")
//    public String showEditForm(@PathVariable Long id,
//                               Model model,
//                               HttpSession session) {
//        User user = (User) session.getAttribute("loggedInUser");
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        Task task = taskService.getTaskById(id);
//        if (task == null) {
//            return "redirect:/dashboard";
//        }
//
//        //  This must be actual task with valid id
//        model.addAttribute("task", task);
//        model.addAttribute("names", userService.getName());
//        return "edit_task";
//    }
//
//    @PostMapping("/tasks/edit/{id}")
//    public String updateTask(@PathVariable Long id,
//                             @ModelAttribute("task") Task updatedTask,
//                             HttpSession session,
//                             Model model) {
//        User user = (User) session.getAttribute("loggedInUser");
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//
//        taskService.updateTask(id, updatedTask, user);
//        return "redirect:/dashboard";
//    }
//
//    @PostMapping("/tasks/delete/{id}")
//    public String deleteTask(@PathVariable Long id, HttpSession session) {
//        User user = (User) session.getAttribute("loggedInUser");
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        taskService.deleteTaskById(id);
//        return "redirect:/dashboard";
//    }

    // Open Edit Task Modal from Dashboard
    @GetMapping("/tasks/edit/{id}")
    public String showEditTaskModal(@PathVariable Long id,
                                    HttpSession session,
                                    Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        Task task = taskService.getTaskById(id);
        if (task == null) {
            model.addAttribute("error", "Task not found.");
            return "redirect:/dashboard";
        }

        model.addAttribute("task", task); // Correct
        model.addAttribute("names", userService.getName()); // For dropdown
        model.addAttribute("openEditModal", true); //  For showing modal
//        model.addAttribute("taskList", taskRepository.findByUser(user)); //  For dashboard
        return "dashboard";
    }


    @PostMapping("/tasks/edit/{id}")
    public String updateTask(@PathVariable Long id,
                             @ModelAttribute("task") Task updatedTask,
                             HttpSession session,
                             Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        taskService.updateTask(id, updatedTask, user);
        return "redirect:/dashboard";
    }


    @PostMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        taskService.deleteTaskById(id);
        return "redirect:/dashboard";
    }


    @GetMapping("/studentdashboard")
    public String showStudentDashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        String loginSuccess = (String) session.getAttribute("loginSuccess");
        if (loginSuccess != null) {
            model.addAttribute("loginSuccess", loginSuccess);
            session.removeAttribute("loginSuccess"); // remove it after showing once
        }


        if (user == null) {
            return "redirect:/login";
        }

        // Filter by student name
        List<Task> tasks = taskService.getTasksByStudentName(user.getName());

        model.addAttribute("taskList", tasks);
        return "studentdashboard"; // this is the name of your Thymeleaf HTML
    }


    @GetMapping("/InProgress")
    public String ShowInProgressTask(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        // Filter by student name
        List<Task> tasks = taskService.getTasksByStudentNameAndStatus(user.getName(), "In Progress");
        model.addAttribute("taskList", tasks);
        return "InProgress";
    }

    @GetMapping("/Completed")
    public String ShowCompletedTask(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        // Filter by student name
        List<Task> tasks = taskService.getTasksByStudentNameAndStatus(user.getName(), "Completed");
        model.addAttribute("taskList", tasks);
        return "Completed";
    }

    @GetMapping("/NotStarted")
    public String ShowNotStartedTask(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        // Filter by student name
        List<Task> tasks = taskService.getTasksByStudentNameAndStatus(user.getName(), "Not Started");
        model.addAttribute("taskList", tasks);
        return "NotStarted";
    }

    @GetMapping("/Delayed")
    public String ShowDelayedTask(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        // Filter by student name
        List<Task> tasks = taskService.getTasksByStudentNameAndStatus(user.getName(), "Delayed");
        model.addAttribute("taskList", tasks);
        return "Delayed";
    }

    @PostMapping("/update-status")
    @ResponseBody
    public ResponseEntity<String> updateTaskStatus(@RequestBody Task task, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        Task existingTask = taskService.getTaskById(task.getId());

        if (existingTask == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        // Optional: you can restrict update only if task belongs to this user
        if (!existingTask.getStudentName().equals(user.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed to update this task");
        }

        existingTask.setStatus(task.getStatus());
        taskService.save(existingTask);

        return ResponseEntity.ok("Status updated successfully");
    }



}




