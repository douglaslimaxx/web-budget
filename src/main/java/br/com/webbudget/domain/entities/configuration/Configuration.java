/*
 * Copyright (C) 2019 Arthur Gregorio, AG.Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.webbudget.domain.entities.configuration;

import br.com.webbudget.domain.entities.PersistentEntity;
import br.com.webbudget.domain.entities.registration.MovementClass;
import lombok.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import static br.com.webbudget.infrastructure.utils.DefaultSchemes.CONFIGURATION;
import static br.com.webbudget.infrastructure.utils.DefaultSchemes.CONFIGURATION_AUDIT;

/**
 * This class represents the configuration of the entire application
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 3.0.0, 16/03/2019
 */
@Entity
@Audited
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "configurations", schema = CONFIGURATION)
@AuditTable(value = "configurations", schema = CONFIGURATION_AUDIT)
public class Configuration extends PersistentEntity {

    @Getter
    @Setter
    @ManyToOne
    @NotNull(message = "{configuration.credit-card-class}")
    @JoinColumn(name = "id_credit_card_class", nullable = false)
    private MovementClass creditCardClass;
}