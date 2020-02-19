/*
 * Copyright (c) 2018 Red Cross Vienna and contributors. All rights reserved.
 *
 * This software may be modified and distributed under the terms of the MIT license. See the LICENSE file for details.
 */

package at.wrk.coceso.plugin.geobroker.contract.broker;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class OneTimeAction implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String type;
    private final String url;
    private final String incidentId;
    private final String additionalData;

    public OneTimeAction(
            final String type,
            final String url,
            @Nullable final String incidentId,
            @Nullable final String additionalData) {
        this.type = Objects.requireNonNull(type, "Type of one-time-action must not be null.");
        this.url = Objects.requireNonNull(url, "URL of one-time-action must not be null.");
        this.incidentId = incidentId;
        this.additionalData = additionalData;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneTimeAction that = (OneTimeAction) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(url, that.url) &&
                Objects.equals(incidentId, that.incidentId) &&
                Objects.equals(additionalData, that.additionalData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, url, incidentId, additionalData);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .append("type", type)
                .append("url", url)
                .append("incidentId", incidentId)
                .append("additionalData", additionalData)
                .toString();
    }
}
