package engine.converted.classes;

import engine.exceptions.*;
import engine.schema.generated.Path;
import engine.schema.generated.Paths;
import java.util.HashMap;
import java.util.Map;

// conversion of the JAXB 'Paths'
public class Trails {

    protected Map<String, Trail> trail;

    public Trails(Paths paths, Map<String, Station> stations) throws InvalidPathNames {
        trail=new HashMap<>();
        for(Path single_path: paths.getPath()){
            addTrail(new Trail(single_path, stations.get(single_path.getFrom().trim().toUpperCase()).getCoordinate().getX(),
                    stations.get(single_path.getFrom().trim().toUpperCase()).getCoordinate().getY(),
                    stations.get(single_path.getTo().trim().toUpperCase()).getCoordinate().getX(),
                    stations.get(single_path.getTo().trim().toUpperCase()).getCoordinate().getY()));
        }
    }

    public Map<String, Trail> getTrails() {
        return trail;
    }

    private void addTrail(Trail single_trail) throws InvalidPathNames {

        if(trail.containsKey(single_trail.getFrom()+single_trail.getTo()))
            throw new InvalidPathNames(single_trail.getFrom().toUpperCase() +" to " + single_trail.getTo().toUpperCase());
        trail.put(single_trail.getFrom().toUpperCase()+single_trail.getTo().toUpperCase(),single_trail);

    }
}
