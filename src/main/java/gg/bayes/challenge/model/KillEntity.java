package gg.bayes.challenge.model;

import gg.bayes.challenge.persistence.KillRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "kill")
public class KillEntity implements GameEvent {

    @Transient
    private KillRepository repository;

    @Id
    @GeneratedValue
    private int id;

    private long matchId;
    private String heroNameKiller;
    private String heroNameVictim;

    @Override
    public void store() {
        repository.save(this);
    }
}
