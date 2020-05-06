package gg.bayes.challenge.model;

import gg.bayes.challenge.persistence.SpellRepository;
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
@Table(name = "spell")
public class SpellEntity implements GameEvent {

    @Transient
    private SpellRepository repository;

    @Id
    @GeneratedValue
    private int id;


    private long matchId;
    private String heroName;
    private String spellName;

    public SpellEntity(long matchId, String heroName, String spellName, SpellRepository repository) {
        this.matchId = matchId;
        this.heroName = heroName;
        this.spellName = spellName;
        this.repository = repository;
    }

    @Override
    public void store() {
        repository.save(this);
    }
}
