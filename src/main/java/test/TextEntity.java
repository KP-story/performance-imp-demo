package test;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Entity
@Table(name = "bigtable")
@DynamicUpdate
@DynamicInsert
class TextEntity implements Persistable<Long> {
    @Column(name = "id")
    @Id
    private Long id;
    @Column(name = "text")
    private String text;

    public void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return true;
    }

    public String getText() {
        return text;
    }

    public TextEntity setText(String text) {
        this.text = text;
        return this;
    }
}
