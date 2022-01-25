package nus.iss.revision15.controller;

import static nus.iss.revision15.Constants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import nus.iss.revision15.service.TaskService;

@Controller
@RequestMapping(path="/task", produces=MediaType.TEXT_HTML_VALUE)
public class TaskController {

    private final Logger logger = Logger.getLogger(TaskController.class.getName());

    @Autowired
    private TaskService taskSvc;

    @PostMapping
    public String addTask(@RequestBody MultiValueMap<String, String> taskPage, Model model) {
        String task = taskPage.getFirst("task");
        String hiddenTasks = taskPage.getFirst("hiddenTasks");
        logger.log(Level.INFO, "hiddenTasks: %s".formatted(hiddenTasks));

        // split the contents into List, delimited by ` (on left of 1 on keyboard)
        List<String> taskList = new ArrayList<>();
        if (null != hiddenTasks && hiddenTasks.trim().length() > 0) {
            // append new task to tasks
            hiddenTasks = "%s".formatted(hiddenTasks.trim()) + DELIMITER + "%s".formatted(task.trim());
            taskList = Arrays.asList(hiddenTasks.split(DELIMITER));
        } else {
            hiddenTasks = task;
            taskList.add(task);
        }

        logger.log(Level.INFO, "hiddenTasks: %s".formatted(hiddenTasks));
        model.addAttribute("hiddenTasks", hiddenTasks);
        logger.log(Level.INFO, "taskList: %s".formatted(taskList));
        model.addAttribute("tasks", taskList);

        return "index";
    }

    @PostMapping("save")
    public String saveTask(@RequestBody MultiValueMap<String, String> taskPage) {
        String hiddenTasks = taskPage.getFirst("hiddenTasks");
        logger.log(Level.INFO, "Saving info: %s".formatted(hiddenTasks));
        taskSvc.save(TASKLIST_KEY, hiddenTasks);
        return "index";
    }

    @PostMapping("get")
    public String getTask(@RequestBody MultiValueMap<String, String> taskPage, Model model) {
        String hiddenTasks = taskSvc.get(TASKLIST_KEY);
        List<String> taskList = new ArrayList<>();
        if (null != hiddenTasks && hiddenTasks.trim().length() > 0)
            taskList = Arrays.asList(hiddenTasks.split(DELIMITER));
        else
            logger.log(Level.WARNING, "There is no saved task list");

        logger.log(Level.INFO, "hiddenTasks: %s".formatted(hiddenTasks));
        model.addAttribute("hiddenTasks", hiddenTasks);
        logger.log(Level.INFO, "taskList: %s".formatted(taskList));
        model.addAttribute("tasks", taskList);
        return "index";
    }

}
