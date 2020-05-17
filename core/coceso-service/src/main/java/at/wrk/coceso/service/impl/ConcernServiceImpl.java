package at.wrk.coceso.service.impl;

import at.wrk.coceso.dto.concern.ConcernBriefDto;
import at.wrk.coceso.dto.concern.ConcernCreateDto;
import at.wrk.coceso.dto.concern.ConcernDto;
import at.wrk.coceso.dto.concern.ConcernUpdateDto;
import at.wrk.coceso.dto.concern.SectionCreateDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.enums.JournalEntryType;
import at.wrk.coceso.entity.journal.ChangesCollector;
import at.wrk.coceso.event.events.ConcernEvent;
import at.wrk.coceso.exceptions.SectionExistsException;
import at.wrk.coceso.mapper.ConcernMapper;
import at.wrk.coceso.repository.ConcernRepository;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.JournalService;
import at.wrk.coceso.utils.AuthenticatedUser;
import at.wrk.fmd.mls.event.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
class ConcernServiceImpl implements ConcernService {

    private final ConcernRepository concernRepository;
    private final ConcernMapper concernMapper;
    private final JournalService journalService;
    private final EventBus eventBus;

    @Autowired
    ConcernServiceImpl(final ConcernRepository concernRepository, final ConcernMapper concernMapper, final JournalService journalService,
            final EventBus eventBus) {
        this.concernRepository = concernRepository;
        this.concernMapper = concernMapper;
        this.journalService = journalService;
        this.eventBus = eventBus;
    }

    @Override
    public Optional<Concern> getConcern(long id) {
        return concernRepository.findById(id);
    }

    @Override
    public ConcernDto getConcern(Concern concern) {
        return concernMapper.concernToDto(concern);
    }

    @Override
    public List<ConcernBriefDto> getAllBrief() {
        return concernRepository.findAll().stream()
                .map(concernMapper::concernToBriefDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConcernDto> getAll() {
        return concernRepository.findAll().stream()
                .map(concernMapper::concernToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Concern> getAllOpen() {
        return concernRepository.findAllOpen();
    }

    @Override
    public Concern getByName(String name) {
        return concernRepository.findByName(name);
    }

    @Override
    public ConcernBriefDto create(final ConcernCreateDto data) {
        log.debug("{}: Triggered creation of concern {}", AuthenticatedUser.getName(), data);

        ChangesCollector changes = new ChangesCollector("concern");
        Concern concern = new Concern();

        changes.put("name", data.getName());
        concern.setName(data.getName());

        if (data.getInfo() != null && !data.getInfo().isEmpty()) {
            changes.put("info", data.getInfo());
            concern.setInfo(data.getInfo());
        }

        concern = concernRepository.save(concern);
        journalService.logConcern(JournalEntryType.CONCERN_CREATE, concern, changes);
        eventBus.publish(new ConcernEvent(concernMapper.concernToDto(concern)));

        return concernMapper.concernToBriefDto(concern);
    }

    @Override
    public void update(final Concern concern, final ConcernUpdateDto data) {
        log.debug("{}: Triggered update of concern {}", AuthenticatedUser.getName(), concern);

        ChangesCollector changes = new ChangesCollector("concern");

        if (data.getName() != null && !data.getName().equals(concern.getName())) {
            changes.put("name", concern.getName(), data.getName());
            concern.setName(data.getName());
        }

        if (data.getInfo() != null && !data.getInfo().equals(concern.getInfo())) {
            changes.put("info", concern.getInfo(), data.getInfo());
            concern.setInfo(data.getInfo());
        }

        if (!changes.isEmpty()) {
            journalService.logConcern(JournalEntryType.CONCERN_UPDATE, concern, changes);
            eventBus.publish(new ConcernEvent(concernMapper.concernToDto(concern)));
        }
    }

    @Override
    public void setClosed(final Concern concern, final boolean close) {
        if (concern.isClosed() == close) {
            // Closed state is already correct, do nothing
            return;
        }

        concern.setClosed(close);

        if (close) {
            log.info("{}: Closed concern {}", AuthenticatedUser.getName(), concern);
            journalService.logConcern(JournalEntryType.CONCERN_CLOSE, concern, null);
        } else {
            log.info("{}: Reopened concern {}", AuthenticatedUser.getName(), concern);
            journalService.logConcern(JournalEntryType.CONCERN_REOPEN, concern, null);
        }
        eventBus.publish(new ConcernEvent(concernMapper.concernToDto(concern)));
    }

    @Override
    public void addSection(final Concern concern, final SectionCreateDto data) {
        if (concern.containsSection(data.getName())) {
            throw new SectionExistsException();
        }

        concern.addSection(data.getName());
        eventBus.publish(new ConcernEvent(concernMapper.concernToDto(concern)));
    }

    @Override
    public void removeSection(final Concern concern, final String section) {
        // TODO: Update units and incidents!
        concern.removeSection(section);
        eventBus.publish(new ConcernEvent(concernMapper.concernToDto(concern)));
    }
}
