package net.dev.jcd.transform;

import net.dev.jcd.warp.data.ToDo;
import net.dev.jcd.warp.data.ToDoRepo;
import net.dev.jcd.warp.log.Loggable;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToDoServices {
	
	@Loggable
	private Logger logger;
	
	@Autowired
	ToDoRepo todoRepo;
	
	
	public ToDo getById(Long id) {		
		logger.info("get by id ["+id+"]");
		ToDo found = todoRepo.findOne(id);
		if (found != null) {
			logger.debug(found.toString());
			return found;
		}
		logger.debug("Not found");
		return null;
	}
	
	public ToDo save(ToDo todo) {
		logger.info("save ["+todo+"]");
		ToDo saved = todoRepo.save(todo);
		logger.info("saved ["+saved+"]");
		return saved;
	}

}

