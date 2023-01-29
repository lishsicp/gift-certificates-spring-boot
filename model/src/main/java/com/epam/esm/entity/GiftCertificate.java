package com.epam.esm.entity;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "gift_certificate")
@NoArgsConstructor
@Getter
@Setter
public class GiftCertificate extends BaseEntity<Long> {

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "price", nullable = false)
  private BigDecimal price;

  @Column(name = "duration", nullable = false)
  private Long duration;

  @Column(name = "create_date")
  private LocalDateTime createDate;

  @Column(name = "last_update_date")
  private LocalDateTime lastUpdateDate;

  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  @JoinTable(
          name = "gift_certificate_tag",
          joinColumns = @JoinColumn(name = "gift_certificate_id"),
          inverseJoinColumns = @JoinColumn(name = "tag_id")
  )
  private List<Tag> tags;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    GiftCertificate that = (GiftCertificate) o;

    return new EqualsBuilder()
            .append(duration, that.duration)
            .append(id, that.id)
            .append(name, that.name)
            .append(description, that.description)
            .append(price, that.price)
            .append(createDate, that.createDate)
            .append(lastUpdateDate, that.lastUpdateDate)
            .append(tags, that.tags)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
            .append(id)
            .append(name)
            .append(description)
            .append(price)
            .append(duration)
            .append(createDate)
            .append(lastUpdateDate)
            .append(tags)
            .toHashCode();
  }

  @Override
  public String toString() {
    return "GiftCertificate{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", price=" + price +
            ", duration=" + duration +
            ", createDate=" + createDate +
            ", lastUpdateDate=" + lastUpdateDate +
            ", tags=" + tags +
            '}';
  }
}
