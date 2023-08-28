package az.monitoringSoftware.monitoringSoftware.domain.entities;


import az.monitoringSoftware.monitoringSoftware.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dailyExpenses")
public class DailyExpense extends BaseEntity {
    private Double amount;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Expense expense;


}
