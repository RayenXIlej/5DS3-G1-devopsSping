package tn.esprit.eventsproject;






import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.LogisticsRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;
import tn.esprit.eventsproject.services.EventServicesImpl;

import java.time.LocalDate;
import java.util.*;

    class EventServiceImplTest {

        @InjectMocks
        private EventServicesImpl eventServices;

        @Mock
        private EventRepository eventRepository;

        @Mock
        private ParticipantRepository participantRepository;

        @Mock
        private LogisticsRepository logisticsRepository;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        void testAddParticipant() {
            Participant participant = new Participant();
            when(participantRepository.save(participant)).thenReturn(participant);

            Participant result = eventServices.addParticipant(participant);

            assertEquals(participant, result);
            verify(participantRepository, times(1)).save(participant);
        }

        @Test
        void testAddAffectEvenParticipantById() {
            int participantId = 1;
            Participant participant = new Participant();
            participant.setEvents(new HashSet<>());
            Event event = new Event();
            when(participantRepository.findById(participantId)).thenReturn(Optional.of(participant));
            when(eventRepository.save(event)).thenReturn(event);

            Event result = eventServices.addAffectEvenParticipant(event, participantId);

            assertEquals(event, result);
            assertTrue(participant.getEvents().contains(event));
            verify(participantRepository, times(1)).findById(participantId);
            verify(eventRepository, times(1)).save(event);
        }

        @Test
        void testAddAffectEvenParticipantWithoutId() {
            Event event = new Event();
            Participant participant = new Participant();
            participant.setIdPart(1);
            event.setParticipants(Set.of(participant));

            Participant fetchedParticipant = new Participant();
            fetchedParticipant.setEvents(new HashSet<>());
            when(participantRepository.findById(participant.getIdPart())).thenReturn(Optional.of(fetchedParticipant));
            when(eventRepository.save(event)).thenReturn(event);

            Event result = eventServices.addAffectEvenParticipant(event);

            assertEquals(event, result);
            assertTrue(fetchedParticipant.getEvents().contains(event));
            verify(participantRepository, times(1)).findById(participant.getIdPart());
            verify(eventRepository, times(1)).save(event);
        }

        @Test
        void testAddAffectLogistics() {
            Logistics logistics = new Logistics();
            String description = "Test Event";
            Event event = new Event();
            event.setLogistics(new HashSet<>());

            when(eventRepository.findByDescription(description)).thenReturn(event);
            when(logisticsRepository.save(logistics)).thenReturn(logistics);

            Logistics result = eventServices.addAffectLog(logistics, description);

            assertEquals(logistics, result);
            assertTrue(event.getLogistics().contains(logistics));
            verify(eventRepository, times(1)).findByDescription(description);
            verify(logisticsRepository, times(1)).save(logistics);
        }

        @Test
        void testGetLogisticsDates() {
            LocalDate startDate = LocalDate.now().minusDays(5);
            LocalDate endDate = LocalDate.now();
            Event event = new Event();
            Logistics logistics = new Logistics();
            logistics.setReserve(true);
            logistics.setPrixUnit(10.0f);
            logistics.setQuantite(2);
            event.setLogistics(Set.of(logistics));

            when(eventRepository.findByDateDebutBetween(startDate, endDate)).thenReturn(List.of(event));

            List<Logistics> result = eventServices.getLogisticsDates(startDate, endDate);

            assertNotNull(result);
            assertTrue(result.contains(logistics));
            verify(eventRepository, times(1)).findByDateDebutBetween(startDate, endDate);
        }

        @Test
        void testCalculCout() {
            Event event = new Event();
            Logistics logistics = new Logistics();
            logistics.setReserve(true);
            logistics.setPrixUnit(10.0f);
            logistics.setQuantite(2);
            event.setLogistics(Set.of(logistics));
            event.setDescription("Test Event");

            when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR))
                    .thenReturn(List.of(event));

            eventServices.calculCout();

            verify(eventRepository, times(1))
                    .findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR);
            verify(eventRepository, times(1)).save(event);

            assertEquals(20.0f, event.getCout());
        }
    }
