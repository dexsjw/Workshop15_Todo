package nus.iss.revision15.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nus.iss.revision15.repository.TaskRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepo;

    public void save(String key, String value) {
        taskRepo.saveTask(key, value);
    }

    public String get(String key) {
        Optional<String> opt = taskRepo.get(key);
        return opt.get();
    }
    
}
