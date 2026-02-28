package com.canse.slave.repos;

import com.canse.slave.entities.TaskEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskEntryRepository extends JpaRepository<TaskEntry, Long> {
}
