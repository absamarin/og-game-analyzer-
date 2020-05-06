package gg.bayes.challenge.model;

import gg.bayes.challenge.persistence.BuyRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "purchase")
@EntityListeners(AuditingEntityListener.class)
public class BuyEntity implements GameEvent {

    @Transient
    private BuyRepository repository;

    @Id
    @GeneratedValue
    private int id;

    private long matchId;
    private String heroName;
    private String itemName;

    @CreatedDate
    @Column(name = "created_date")
    private Date createdDate;

    public BuyEntity(long matchId, String heroName, String itemName, BuyRepository repository) {
        this.matchId = matchId;
        this.heroName = heroName;
        this.itemName = itemName;
        this.repository = repository;
    }

    @Override
    public void store() {
        repository.save(this);
    }
}
