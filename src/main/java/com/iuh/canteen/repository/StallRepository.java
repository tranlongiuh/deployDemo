package com.iuh.canteen.repository;

import com.iuh.canteen.entity.Stall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StallRepository extends JpaRepository<Stall, Long> {

    Stall findByManagerId(Long managerId);

    Stall findFirstByOrderByIdAsc();
}
