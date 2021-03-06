/*
 * Copyright (C) 2018 Arthur Gregorio, AG.Software
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
package br.com.webbudget.domain.repositories.financial;

import br.com.webbudget.application.components.ui.filter.PeriodMovementFilter;
import br.com.webbudget.application.components.ui.table.Page;
import br.com.webbudget.domain.entities.financial.PeriodMovement;
import br.com.webbudget.domain.entities.financial.PeriodMovement_;
import br.com.webbudget.domain.entities.registration.Contact;
import br.com.webbudget.domain.entities.registration.Contact_;
import br.com.webbudget.domain.entities.registration.FinancialPeriod;
import br.com.webbudget.domain.entities.registration.FinancialPeriod_;
import br.com.webbudget.domain.repositories.DefaultRepository;
import org.apache.deltaspike.data.api.EntityGraph;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.Criteria;

import javax.persistence.criteria.JoinType;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * The {@link PeriodMovement} repository
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 3.0.0, 04/12/2018
 */
@Repository
public interface PeriodMovementRepository extends DefaultRepository<PeriodMovement> {

    /**
     * {@inheritDoc}
     *
     * @param id
     * @return
     */
    @Override
    @EntityGraph(value = "Movement.full")
    Optional<PeriodMovement> findById(Long id);

    /**
     * Find a {@link PeriodMovement} by the code
     *
     * @param movementCode used as a filter
     * @return an {@link Optional} of the {@link PeriodMovement}
     */
    @EntityGraph(value = "Movement.full")
    Optional<PeriodMovement> findByCode(String movementCode);

    /**
     * Use this method to find all {@link PeriodMovement} using the lazy load strategy
     *
     * @param filter the {@link PeriodMovementFilter}
     * @param start starting row
     * @param pageSize page size
     * @return the {@link Page} filled with the {@link PeriodMovement} found
     */
    default Page<PeriodMovement> findAllBy(PeriodMovementFilter filter, int start, int pageSize) {

        final int totalRows = this.countPages(filter);

        final Criteria<PeriodMovement, PeriodMovement> criteria = this.buildCriteria(filter);

        criteria.orderDesc(PeriodMovement_.createdOn);

        final List<PeriodMovement> data = criteria.createQuery()
                .setFirstResult(start)
                .setMaxResults(pageSize)
                .getResultList();

        return Page.of(data, totalRows);
    }

    /**
     * This method is used to count the total of rows found with the given filter
     *
     * @param filter the {@link PeriodMovementFilter}
     * @return total size of the pages
     */
    @SuppressWarnings("unchecked")
    default int countPages(PeriodMovementFilter filter) {
        return this.buildCriteria(filter)
                .select(Long.class, count(PeriodMovement_.id))
                .getSingleResult()
                .intValue();
    }

    /**
     * This method is used to build the {@link Criteria} used to find the {@link PeriodMovement}
     *
     * @param filter the {@link PeriodMovementFilter}
     * @return the {@link Criteria} with the restrictions to find the {@link PeriodMovement}
     */
    @SuppressWarnings("unchecked")
    default Criteria<PeriodMovement, PeriodMovement> buildCriteria(PeriodMovementFilter filter) {

        final Criteria<PeriodMovement, PeriodMovement> criteria = this.criteria();

        // set the movement state filter if present
        if (filter.getPeriodMovementState() != null) {
            criteria.eq(PeriodMovement_.periodMovementState, filter.getPeriodMovementState());
        }

        // the movement type filter if present
        if (filter.getPeriodMovementType() != null) {
            criteria.eq(PeriodMovement_.periodMovementType, filter.getPeriodMovementType());
        }

        // now the OR filters, more generic
        if (isNotBlank(filter.getValue())) {

            final String anyFilter = this.likeAny(filter.getValue());

            criteria.or(this.criteria().eq(PeriodMovement_.code, anyFilter));
            criteria.or(this.criteria().likeIgnoreCase(PeriodMovement_.description, anyFilter));
            criteria.or(this.criteria().likeIgnoreCase(PeriodMovement_.identification, anyFilter));
            criteria.or(this.criteria().join(PeriodMovement_.financialPeriod,
                    where(FinancialPeriod.class).likeIgnoreCase(FinancialPeriod_.identification, anyFilter)));
            criteria.or(this.criteria().join(PeriodMovement_.contact,
                    where(Contact.class, JoinType.LEFT).likeIgnoreCase(Contact_.name, anyFilter)));

            // if we can cast the value of the filter to decimal, use this as filter
            filter.valueToBigDecimal()
                    .ifPresent(value -> criteria.or(this.criteria().eq(PeriodMovement_.value, value)));
        }

        return criteria;
    }
}
