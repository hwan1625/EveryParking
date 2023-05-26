package com.everyparking.server.data.repository;

import com.everyparking.server.data.entity.Member;
import com.everyparking.server.data.entity.RoleType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(String userId);

    Optional<List<Member>> findAllByRoleType(RoleType roleType);

    Optional<Member> findByParkingInfoId(Long parkingInfoId);
    Optional<Member> findByCarId(Long carId);
}
