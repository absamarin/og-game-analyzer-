package gg.bayes.challenge.model;

import gg.bayes.challenge.persistence.HitRepository;
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
@Table(name = "hit")
public class HitEntity implements GameEvent {


    @Transient
    private HitRepository repository;

    @Id
    @GeneratedValue
    private int id;


    private long matchId;
    private String heroName;
    private String heroNameVictim;
    private int damage;


    @Override
    public void store() {
        repository.save(this);
    }
}
