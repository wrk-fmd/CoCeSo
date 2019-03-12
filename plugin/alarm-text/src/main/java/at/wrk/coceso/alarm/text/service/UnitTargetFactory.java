package at.wrk.coceso.alarm.text.service;

import at.wrk.coceso.alarm.text.service.normalizer.NumberNormalizer;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Component
public class UnitTargetFactory {
    private static final Logger LOG = LoggerFactory.getLogger(UnitTargetFactory.class);
    private static final String DEFAULT_NUMBER_NORMALIZER = "tel";

    private final Map<String, NumberNormalizer> numberNormalizers;
    private final Collection<String> supportedUriSchemas;

    @Autowired
    public UnitTargetFactory(final List<NumberNormalizer> numberNormalizers) {
        this.numberNormalizers = numberNormalizers
                .stream()
                .collect(toMap(NumberNormalizer::getSupportedUriSchema, Function.identity()));
        this.supportedUriSchemas = ImmutableSet.copyOf(
                this.numberNormalizers
                        .keySet()
                        .stream()
                        .map(x -> x + ":")
                        .collect(toSet())
        );
    }

    public Map<String, List<String>> getValidTargetsOfUnitStream(final Stream<Unit> unitStream) {
        return unitStream
                .map(this::getTargetsOfUnit)
                .flatMap(this::streamContacts)
                .collect(groupingBy(TypedTargetNumber::getType, mapping(TypedTargetNumber::getTarget, toList())));
    }

    private List<String> getTargetsOfUnit(final Unit unit) {
        List<String> contacts = unit.getCrew()
                .stream()
                .map(User::getContact)
                .collect(Collectors.toList());

        if (unit.getAni() != null) {
            contacts.add(unit.getAni());
        }

        return ImmutableList.copyOf(contacts);
    }

    private Stream<TypedTargetNumber> streamContacts(final Collection<String> unvalidatedTargets) {
        return unvalidatedTargets
                .stream()
                .flatMap(UnitTargetFactory::splitByLineBreak)
                .map(this::normalizeIfNecessary)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private Optional<TypedTargetNumber> normalizeIfNecessary(final String targetUri) {
        Optional<TypedTargetNumber> targetNumber;
        if (StringUtils.startsWithAny(targetUri, getSupportedUriSchemas())) {
            String[] splittedNumber = targetUri.split(":");
            if (splittedNumber.length == 2) {
                String targetType = splittedNumber[0];
                String rawTargetNumber = splittedNumber[1];
                targetNumber = createTypedTargetNumberIfNotBlank(targetType, rawTargetNumber);
            } else {
                LOG.info("Target URI '{}' could not be parsed for a typed target number.");
                targetNumber = Optional.empty();
            }
        } else {
            targetNumber = createTypedTargetNumberIfNotBlank(DEFAULT_NUMBER_NORMALIZER, targetUri);
        }

        return targetNumber;
    }

    private Optional<TypedTargetNumber> createTypedTargetNumberIfNotBlank(final String targetType, final String rawTargetNumber) {
        Optional<TypedTargetNumber> targetNumber;

        String normalizedTargetNumber = numberNormalizers.get(targetType).normalize(rawTargetNumber);
        if (StringUtils.isNotBlank(normalizedTargetNumber)) {
            targetNumber = Optional.of(new TypedTargetNumber(targetType, normalizedTargetNumber));
        } else {
            targetNumber = Optional.empty();
        }

        return targetNumber;
    }

    private String[] getSupportedUriSchemas() {
        return supportedUriSchemas.toArray(new String[1]);
    }

    private static Stream<String> splitByLineBreak(final String contactString) {
        return Stream.of(StringUtils.split(contactString, "\n"));
    }

    private static class TypedTargetNumber {
        private final String type;
        private final String target;

        TypedTargetNumber(final String type, final String target) {
            this.type = type;
            this.target = target;
        }

        String getType() {
            return type;
        }

        String getTarget() {
            return target;
        }
    }
}
