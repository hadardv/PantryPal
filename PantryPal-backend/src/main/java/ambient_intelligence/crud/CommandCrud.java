package ambient_intelligence.crud;

import org.springframework.data.mongodb.repository.MongoRepository;

import ambient_intelligence.data.CommandEntity;

public interface CommandCrud extends MongoRepository<CommandEntity, String>{

}

