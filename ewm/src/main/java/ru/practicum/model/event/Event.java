package ru.practicum.model.event;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.category.Category;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "initiator", nullable = false)
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "categories_id", nullable = false)
    private Category category;

    @Column(name = "title", length = 120, nullable = false)
    private String title;

    @Column(name = "annotation", length = 2000, nullable = false)
    private String annotation;

    @Column(name = "description", length = 7000, nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 20, nullable = false)
    private State state;

    @Embedded
    private Location location;

    @Column(name = "participant_limit", nullable = false)
    private Long participantLimit;

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
}
