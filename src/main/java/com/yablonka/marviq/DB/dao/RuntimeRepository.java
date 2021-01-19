package com.yablonka.marviq.DB.dao;

import com.yablonka.marviq.DB.model.Runtime;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RuntimeRepository extends CrudRepository<Runtime, Long> {

    List<Runtime> findAllByMachineName(String machineName);

}
