package me.rumoredtuna.ngma.schedule;

import me.rumoredtuna.ngma.account.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("select s from Schedule s where s.owner.id = :accountId order by s.dateTime")
    List<Schedule> findAllByOwner(Long accountId);

    @Query("select s from Schedule s where s.owner.id = :accountId order by s.dateTime")
    List<Schedule> findAllByOwner(Long accountId, Pageable pageable);

    @Query("select s from Schedule s where s.owner.id = :accountId " +
            "or s.owner.id = (select a.lover.id from Account a where a.id = :accountId) order by s.dateTime")
    List<Schedule> findAllByCouple(Long accountId);

    @Query("select s from Schedule s where s.owner.id = :accountId " +
            "or s.owner.id = (select a.lover.id from Account a where a.id = :accountId) order by s.dateTime")
    List<Schedule> findAllByCouple(Long accountId, Pageable pageable);
}
