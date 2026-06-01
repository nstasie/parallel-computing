package com.coworking.booking.model;

import com.coworking.booking.model.enums.WorkspaceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Сутність робочого простору (місця) у коворкінгу.
 * Може бути індивідуальним столом (DESK) або переговорною кімнатою (MEETING_ROOM).
 */
@Entity
@Table(name = "workspaces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workspace {

    /** Унікальний ідентифікатор робочого простору. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Тип робочого простору: DESK або MEETING_ROOM. */
    @NotNull(message = "Тип робочого простору є обов'язковим")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkspaceType type;

    /** Максимальна кількість осіб, які можуть одночасно використовувати простір. */
    @NotNull(message = "Місткість є обов'язковим параметром")
    @Min(value = 1, message = "Місткість повинна бути не менше 1")
    @Column(nullable = false)
    private Integer capacity;

    /**
     * Поточна доступність простору для бронювання.
     * true — простір вільний, false — зарезервований або недоступний.
     */
    @Column(nullable = false)
    private Boolean isAvailable = true;
}
