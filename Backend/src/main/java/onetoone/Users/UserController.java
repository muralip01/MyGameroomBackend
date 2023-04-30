package onetoone.Users;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.OneToMany;
import java.util.List;
import java.util.Set;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    private String rePassword = "{\"message\":\"make password more than three characters\"}";

    private String usernameTaken = "{\"message\":\"username already has taken\"}";

    private String wrongPassword = "{\"message\":\"wrong password\"}";

    private String noUserName = "{\"message\":\"No user name exists or username typed wrong \"}";

    @GetMapping(path = "/users")
    List<User> getAllUsers() {
        return userRepository.findAll();
    }
	
	
    @GetMapping(path = "/users/{id}")
    User getUserById(@PathVariable int id) {
        return userRepository.findById(id);
    }


    @PostMapping(path = "/logIn")
    String logIn(@RequestBody User user) {

        if(user == null) {
            return failure;
        }
        User check = userRepository.findByName(user.getName());
        if (check == null) {
            return noUserName;
        }
        if (check.getPassword().equals(user.getPassword())) {
            return "{\"message\":\"success\", \"userId\":" + check.getId() + "}";
        }
        else {
            return wrongPassword;
        }
    }
	

    @PostMapping(path = "/createUsers")
    String createUser(@RequestBody User user) {
        if (user == null) {
            return failure;
        }
        if (user.getPassword() == null || user.getPassword().equals("")) {
            return failure;
        }
        if (user.getPassword().length() < 4) {
            return rePassword;
        }

        User check = userRepository.findByName(user.getName());

        if (check == null) {

            userRepository.save(user);
            return success;

        } else {

            if (check.getName().equals(user.getName())) {
                return usernameTaken;
            } else {
                return failure;
            }
        }

    }


    @PutMapping("/users/{id}")
    User updateUser(@PathVariable int id, @RequestBody User request) {
        User user = userRepository.findById(id);
        if (user == null)
            return null;
        userRepository.save(request);
        return userRepository.findById(id);
    }
	
	
    @DeleteMapping(path = "/users/{id}")
    String deleteUser(@PathVariable int id) {
        userRepository.deleteById(id);
        return success;
    }

        return success;
    }
}

