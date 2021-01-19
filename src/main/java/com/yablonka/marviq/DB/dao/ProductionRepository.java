package com.yablonka.marviq.DB.dao;


import com.yablonka.marviq.DB.model.Production;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductionRepository extends CrudRepository<Production, Long> {
    List<Production> findAllByMachineNameAndVariableName(String machineName, String variableName);

}
