package uo.asw.dbManagement.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import uo.asw.dbManagement.model.Agent;

public interface AgentsRepository extends CrudRepository<Agent, Long>{
	
	@Query("SELECT a FROM Agent a WHERE a.identifier = ?1")
	Agent findByLogin(String login);
}
