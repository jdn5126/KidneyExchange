package KidneyExchange;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;

public class TestResultsSerializer implements JsonSerializer<TestResults> {
    @Override
    public JsonElement serialize( TestResults results, Type type, JsonSerializationContext context ) {
        JsonObject json = new JsonObject();
        json.addProperty( "numPairsOperated", results.getNumPairsOperated() );
        json.addProperty( "numPairsTotal", results.getNumPairsTotal() );
        json.addProperty( "operationRate", results.getOperationRate() );
        json.addProperty( "runDuration", results.getRunDuration() );

        List<List<Long>> roundDurations = results.getRoundDurations();
        JsonObject durationsObj = new JsonObject();
        for( int iHospital = 0; iHospital < roundDurations.size(); ++iHospital ) {
            durationsObj.add( "hospital" + (iHospital + 1), context.serialize( roundDurations.get( iHospital ) ) );
        }

        json.add( "roundDurations", durationsObj );
        return json;
    }
}
