package at.wrk.cocecl.dto.demo;

import at.wrk.cocecl.dto.Incident;
import at.wrk.cocecl.dto.Unit;
import at.wrk.cocecl.dto.main.GetActiveIncidentsAnswer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @deprecated For demo purposes only!
 */
@Deprecated
public class ActiveIncidentsExample {

    public static void main(String[] args) {
        // Pretty printing setting only for demo!
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Collection<Incident> incidents = prepareIncidents();
        GetActiveIncidentsAnswer answer = GetActiveIncidentsAnswer.create(true, incidents);

        println("Answer is: %s", answer.toString());

        String serializedAnswer = gson.toJson(answer);

        println("Answer as json: %s", serializedAnswer);

        GetActiveIncidentsAnswer deserializedAnswer = gson.fromJson(serializedAnswer, GetActiveIncidentsAnswer.class);

        println("Answer deserialized: %s", deserializedAnswer);
    }

    private static Collection<Incident> prepareIncidents() {
        Collection<Incident> incidents = new ArrayList<>();
        incidents.add(prepareIncident(123));
        incidents.add(prepareIncident(234));
        return incidents;
    }

    private static Incident prepareIncident(int id) {
        Incident incident = Incident.create(id);
        List<Unit> units = Collections.singletonList(Unit.create(id * 2 - 5));
        incident.setUnits(units);
        incident.setBlue(id % 2 == 0);
        incident.setCaller("Just any caller");
        return incident;
    }

    private static void println(final String message, final Object... parameters) {
        System.out.println(String.format(message, parameters));
    }
}
